@file:Suppress("unused")

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import me.modmuss50.mpp.platforms.modrinth.ModrinthEnvironment
import java.util.*

sealed class Loader(val id: String) {
	abstract val modManifestPath: String
	abstract val excludedResources: List<String>
	abstract val nestedLibrariesPath: String

	open val isFabricLike: Boolean = false

	abstract fun generateManifest(ctx: Context, includeBundled: Boolean): String

	object Fabric : Loader("fabric") {
		override val isFabricLike = true
		override val modManifestPath = "fabric.mod.json"
		override val nestedLibrariesPath = "META-INF/jars/"
		override val excludedResources = listOf(
			"META-INF/mods.toml", "META-INF/neoforge.mods.toml", "META-INF/accesstransformer.cfg", ".cache"
		)

		override fun generateManifest(ctx: Context, includeBundled: Boolean): String {
			val manifest = FabricManifest(
				id = ctx.modId,
				name = ctx.modName,
				version = ctx.baseVersion,
				authors = ctx.authors,
				contributors = ctx.contributors,
				contact = mapOf(
					"sources" to ctx.sourcesUrl, "issues" to ctx.issuesUrl, "homepage" to ctx.homepageUrl
				),
				custom = ctx.discordUrl.takeIf { it.isNotEmpty() }?.let { url ->
					buildJsonObject {
						putJsonObject("modmenu") {
							putJsonObject("links") {
								put("modmenu.discord", url)
							}
						}
					}
				},
				description = ctx.description,
				icon = "${ctx.modId}.png",
				license = ctx.licenseName,
				environment = when (ctx.environment) {
					ModrinthEnvironment.CLIENT_ONLY, ModrinthEnvironment.SINGLEPLAYER_ONLY -> "client"

					ModrinthEnvironment.DEDICATED_SERVER_ONLY -> "server"

					ModrinthEnvironment.SERVER_ONLY, ModrinthEnvironment.SERVER_ONLY_CLIENT_OPTIONAL,
					ModrinthEnvironment.CLIENT_ONLY_SERVER_OPTIONAL, ModrinthEnvironment.CLIENT_AND_SERVER,
					ModrinthEnvironment.CLIENT_OR_SERVER_PREFERS_BOTH, ModrinthEnvironment.CLIENT_OR_SERVER -> "*"
				},
				accessWidener = if (ctx.currentMcVersion == "1.20.1") "${ctx.modId}.accesswidener" else null,
				entrypoints = mapOf(
					"main" to listOf("${ctx.modGroup}.${ctx.modId}.platform.fabric.FabricArsElixirum"),
					"client" to listOf("${ctx.modGroup}.${ctx.modId}.platform.fabric.client.FabricArsElixirumClient")
				),
				mixins = listOf("${ctx.modId}.mixins.json", "${ctx.modId}.fabric.mixins.json"),
				depends = ctx.extension.dependencies.requiredFor(includeBundled).associate { it.modid.get() to it.fabricLikeVersionRange.get() },
				recommends = ctx.extension.dependencies.optional.associate { it.modid.get() to it.fabricLikeVersionRange.get() },
				breaks = ctx.extension.dependencies.incompatible.associate { it.modid.get() to it.fabricLikeVersionRange.get() },
				provides = ctx.extension.dependencies.embeds.map { it.modid.get() }
			)
			return JSON.encodeToString(manifest)
		}
	}

	sealed class ForgeLike(id: String) : Loader(id) {
		override val excludedResources = listOf(
			"fabric.mod.json", "*.accesswidener", ".cache"
		)

		override val nestedLibrariesPath = "META-INF/jarjar/"

		//open val modLoaderId: String = "kotlinforforge"
		open val modLoaderId: String = "javafml"

		override fun generateManifest(ctx: Context, includeBundled: Boolean): String {
			val forgeDeps = mutableListOf<ForgeDependency>()

			fun addDeps(container: Iterable<Dependency>, type: String) {
				container.forEach {
					forgeDeps.add(
						ForgeDependency(
							modId = it.modid.get(),
							side = it.environment.get().uppercase(Locale.getDefault()),
							versionRange = it.forgeLikeVersionRange.get(),
							mandatory = type == "required",
							type = type
						)
					)
				}
			}

			addDeps(ctx.extension.dependencies.requiredFor(includeBundled), "required")
			addDeps(ctx.extension.dependencies.optional, "optional")
			addDeps(ctx.extension.dependencies.incompatible, "incompatible")

			val manifest = ForgeManifest(
				modLoader = modLoaderId,
				//loaderVersion = "[${ctx.project.prop("deps.kotlinforforge")},)",
				loaderVersion = if (this is Forge) "[47,)" else "[4,)",
				license = ctx.licenseName,
				issueTrackerURL = ctx.issuesUrl,
				clientSideOnly = !ctx.environmentPhysicalServer,
				mods = listOf(
					ForgeMod(
						modId = ctx.modId,
						displayName = ctx.modName,
						version = ctx.baseVersion,
						displayURL = ctx.homepageUrl,
						updateJSONURL = ctx.updateUrl,
						modUrl = ctx.homepageUrl,
						logoFile = "${ctx.modId}.png",
						authors = ctx.authors.joinToString(", "),
						credits = ctx.credits,
						description = ctx.description
					)
				),
				dependencies = mapOf(ctx.modId to forgeDeps),
				mixins = listOf(ForgeMixin("${ctx.modId}.mixins.json"), ForgeMixin("${ctx.modId}.${id}.mixins.json")),
				accessTransformers = if (this is Forge) listOf(ForgeAccessTransformer("META-INF/accesstransformer.cfg")) else emptyList()
			)

			return TOML.encodeToString(manifest)
		}
	}

	object NeoForge : ForgeLike("neoforge") {
		override val modManifestPath = "META-INF/neoforge.mods.toml"
		override val excludedResources = super.excludedResources + listOf("META-INF/mods.toml", "META-INF/accesstransformer.cfg")
	}

	object Forge : ForgeLike("forge") {
		override val modManifestPath = "META-INF/mods.toml"
		override val excludedResources = super.excludedResources + "META-INF/neoforge.mods.toml"
		val mixinConfigAttribute = "MixinConfigs"
	}

	companion object {
		fun of(id: String): Loader = when (id) {
			"fabric" -> Fabric
			"neoforge" -> NeoForge
			"forge" -> Forge
			else -> error("Unknown loader: '$id'")
		}
	}
}
