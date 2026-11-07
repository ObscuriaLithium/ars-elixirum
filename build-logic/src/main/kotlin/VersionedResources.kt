import org.gradle.api.Project
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import org.gradle.kotlin.dsl.named
import org.gradle.language.jvm.tasks.ProcessResources
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

data class ResourceRule(
	val predicate: String,
	val path: String
)

data class ResourceMapping(
	val source: String,
	val rules: List<ResourceRule>
)

fun Project.applyVersionedResources(
	ctx: Context,
	mappings: List<ResourceMapping>
) {
	val resolvedMappings = mappings.map { mapping ->
		val destination = mapping.rules
			.firstOrNull { it.predicate == "else" || ctx.stonecutter.eval(ctx.currentMcVersion, it.predicate) }
			?.path ?: error("Versioned resources: no suitable rule for '${mapping.source}' for version ${ctx.currentMcVersion}")
		mapping.source.format(ctx) to destination.format(ctx)
	}

	tasks.named<ProcessResources>("processResources") {
		doLast {
			resolvedMappings.forEach { (sourcePath, destinationPath) ->
				val source = destinationDir.resolve(sourcePath)
				val target = destinationDir.resolve(destinationPath)
				if (!source.exists()) return@forEach
				source.copyRecursively(target, overwrite = true)
				source.deleteRecursively()
			}
			if (ctx.stonecutter.eval(ctx.currentMcVersion, ">=1.21")) migrateMinecraftResources(destinationDir, ctx.loader)
		}
	}
}

private fun migrateMinecraftResources(directory: File, loader: Loader) {
	for (source in directory.walkTopDown().filter { it.isFile }.toList()) {
		val segments = source.relativeTo(directory).invariantSeparatorsPath.split('/').toMutableList()
		val data = segments.indexOf("data")
		if (source.name.endsWith(".mixins.json")) {
			val config = JSON.parseToJsonElement(source.readText()).jsonObject
			val updated = config + ("compatibilityLevel" to JsonPrimitive("JAVA_21"))
			source.writeText(JSON.encodeToString(JsonObject.serializer(), JsonObject(if (loader is Loader.NeoForge) updated - "refmap" else updated)))
		}
		if (data >= 0 && segments.size > data + 2) {
			val kind = data + 2
			when (segments[kind]) {
				"recipes" -> {
					segments[kind] = "recipe"
					val recipe = JSON.parseToJsonElement(source.readText()).jsonObject
					val result = recipe["result"] as? JsonObject
					if (result?.containsKey("item") == true) {
						val stack = JsonObject(result.filterKeys { it != "item" } + ("id" to result.getValue("item")))
						source.writeText(JSON.encodeToString(JsonObject.serializer(), JsonObject(recipe + ("result" to stack))))
					}
				}
				"loot_tables" -> segments[kind] = "loot_table"
				"tags" -> if (segments.size > kind + 1) {
					val names = mapOf("blocks" to "block", "items" to "item", "entity_types" to "entity_type", "fluids" to "fluid", "game_events" to "game_event")
					segments[kind + 1] = names[segments[kind + 1]] ?: segments[kind + 1]
				}
			}
		}
		if (source.name == "pack.mcmeta") {
			val metadata = JSON.parseToJsonElement(source.readText()).jsonObject
			val pack = metadata.getValue("pack").jsonObject
			val format = if (source.parentFile == directory) 34 else 48
			val updated = JsonObject(pack + mapOf("pack_format" to JsonPrimitive(format), "supported_formats" to JsonArray(listOf(JsonPrimitive(34), JsonPrimitive(48)))))
			source.writeText(JSON.encodeToString(JsonObject.serializer(), JsonObject(metadata + ("pack" to updated))))
		}
		val target = directory.resolve(segments.joinToString("/"))
		if (target != source) {
			target.parentFile.mkdirs()
			Files.move(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
		}
	}
}

private fun String.format(ctx: Context): String {
	return this.replace("@MODID", ctx.modId)
}
