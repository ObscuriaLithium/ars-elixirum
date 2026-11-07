import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.*
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.plugins.ide.idea.model.IdeaModel
import javax.inject.Inject
import java.util.zip.ZipFile

abstract class ModPlatformPlugin @Inject constructor() : Plugin<Project> {

	override fun apply(project: Project) = with(project) {
		val inferredLoader = Loader.of(project.buildFile.name.substringAfter('.').replace(".gradle.kts", ""))

		val extension = extensions.create("platform", ModPlatformExtension::class.java).apply {
			loader.convention(inferredLoader.id)
		}

		when (inferredLoader) {
			is Loader.Fabric -> {
				extension.jarTask.convention("remapJar")
				extension.sourcesJarTask.convention("remapSourcesJar")
			}

			is Loader.Forge -> {
				extension.jarTask.convention("reobfJar")
				extension.sourcesJarTask.convention("sourcesJar")
			}

			else -> {
				extension.jarTask.convention("jar")
				extension.sourcesJarTask.convention("sourcesJar")
			}
		}

		apply(plugin = "me.modmuss50.mod-publish-plugin")

		val ctx = Context(
			project = this,
			extension = extension,
			loader = Loader.of(extension.loader.get()),
			stonecutter = project.sc
		)
		configureProject(ctx)
	}

	private fun Project.configureProject(ctx: Context) {
		version = ctx.fullVersion
		group = ctx.modGroup

		listOf("java", "idea").forEach { apply(plugin = it) }
		ctx.extension.requiredJava.set(ctx.javaVersion)

		if (ctx.loader.isFabricLike) {
			ctx.extension.dependencies {
				required("java") { fabricLikeVersionRange = ">=${ctx.javaVersion.majorVersion}" }
			}
		}

		configureSources(ctx)
		configureDependencies(ctx)
		registerGenerateManifestTask(ctx)
		configureJarTask(ctx)
		registerLiteJarTask(ctx)
		configureIdea()
		configureProcessResources(ctx)
		applyVersionedResources(ctx, versionedResourceMappings)
		configureJava(ctx)
		registerBuildAndCollectTask(ctx)
		registerJarCheck(ctx)

		configureModPublishing(ctx)

		if (envTrue("PUB_MAVEN_ENABLE")) {
			configureLocalMavenPublishing(ctx)
			if (envTrue("PUB_MAVEN_CENTRAL_ENABLE")) {
				configureMavenCentralPublishing(ctx)
			}
		}
	}

	private fun Project.configureJava(ctx: Context) {
		tasks.withType<JavaCompile>().configureEach { options.encoding = "UTF-8" }
		tasks.withType<Javadoc>().configureEach { options.encoding = "UTF-8" }
		extensions.configure<JavaPluginExtension>("java") {
			withSourcesJar()
			withJavadocJar()
			sourceCompatibility = ctx.javaVersion
			targetCompatibility = ctx.javaVersion
			toolchain {
				languageVersion.set(JavaLanguageVersion.of(ctx.javaVersion.majorVersion))
			}
		}
	}

	private fun Project.registerGenerateManifestTask(ctx: Context) {
		val manifestOutputDir = layout.buildDirectory.dir("generated/modManifest")

		abstract class GenerateModManifestTask : DefaultTask() {
			@get:Input
			abstract val content: Property<String>

			@get:OutputFile
			abstract val outputFile: RegularFileProperty

			@TaskAction
			fun generate() {
				val file = outputFile.get().asFile
				file.parentFile.mkdirs()
				file.writeText(content.get())
			}
		}

		val generateTask = tasks.register<GenerateModManifestTask>("generateModManifest") {
			content.set(providers.provider { ctx.loader.generateManifest(ctx, includeBundled = false) })
			outputFile.set(layout.buildDirectory.file("generated/modManifest/${ctx.loader.modManifestPath}"))
		}

		the<JavaPluginExtension>().sourceSets.named("main") { resources.srcDir(manifestOutputDir) }
		tasks.named<ProcessResources>("processResources") { dependsOn(generateTask) }
	}

	private fun Project.configureProcessResources(ctx: Context) {
		val javaVersionValue = "JAVA_${ctx.javaVersion.majorVersion}"
		val modId = prop("mod.id")
		val excluded = ctx.loader.excludedResources
		tasks.named<ProcessResources>("processResources") {
			dependsOn(tasks.named("stonecutterGenerate"))
			inputs.properties(mapOf("java" to javaVersionValue, "modid" to modId, "mod_name" to ctx.modName))
			filesMatching("*.mixins.json") {
				expand(
					"java" to javaVersionValue,
					"modid" to modId,
					"mod_id" to modId
				)
			}
			filesMatching("pack.mcmeta") { expand("mod_name" to ctx.modName) }
			exclude(excluded)
			for (loader in listOf("fabric", "forge", "neoforge").filter { it != ctx.loader.id })
				exclude("$modId.$loader.mixins.json")
		}
	}

	private fun Project.configureJarTask(ctx: Context) {
		val generateTask = tasks.named("generateModManifest")
		tasks.withType<Jar>().configureEach {
			archiveBaseName.set(ctx.modId)
			if (name != "remapJar" && name != "remapSourcesJar") from(rootProject.file("LICENSE.md"))
			dependsOn(generateTask)
			if (ctx.loader is Loader.Forge) {
				manifest.attributes(ctx.loader.mixinConfigAttribute to "${ctx.modId}.mixins.json,${ctx.modId}.forge.mixins.json")
			}
		}
	}

	private fun Project.configureIdea() {
		extensions.configure<IdeaModel>("idea") {
			module {
				isDownloadJavadoc = true
				isDownloadSources = true
			}
		}
	}

	private fun Project.configureSources(ctx: Context) {
		extensions.configure<JavaPluginExtension> {
			sourceSets.named("main") {
				for (loader in listOf("fabric", "forge", "neoforge").filter { it != ctx.loader.id })
					java.exclude("dev/obscuria/elixirum/platform/$loader/**")
				java.srcDir(rootProject.file("src/${ctx.currentMcVersion}/java"))
				resources.srcDir(rootProject.file("src/generated/resources"))
			}
		}
	}

	private fun Project.configureDependencies(ctx: Context) {
		repositories {
			mavenCentral()
			strictMaven("https://api.modrinth.com/maven", "maven.modrinth")
			maven("https://maven.neoforged.net/releases/")
		}
		dependencies {
			if (ctx.currentMcVersion == "1.21.1") add("implementation", "org.jetbrains.kotlin:kotlin-stdlib:2.3.10")
			add("implementation", "org.jetbrains:annotations:24.1.0")
			add("implementation", "com.google.code.findbugs:jsr305:3.0.1")
			add("compileOnly", "org.checkerframework:checker-qual:3.43.0")
			add("annotationProcessor", "org.checkerframework:checker:3.43.0")
			add("compileOnly", "org.projectlombok:lombok:1.18.34")
			add("annotationProcessor", "org.projectlombok:lombok:1.18.34")
		}
		ctx.extension.dependencies {
			required("fragmentum") {
				slug("fragmentum", "fragmentum${when (ctx.loader) { Loader.Fabric -> "-fabric"; Loader.NeoForge -> "-neoforge"; else -> "" }}")
				fabricLikeVersionRange.set(">=${prop("deps.fragmentum")}")
				forgeLikeVersionRange.set("[${prop("deps.fragmentum")},)")
			}
			if (ctx.currentMcVersion == "1.20.1") required("archivist") {
				slug("archivist-api", "archivist")
				fabricLikeVersionRange.set(">=${prop("deps.archivist")}")
				forgeLikeVersionRange.set("[${prop("deps.archivist")},)")
			}
		}
	}

	private fun Project.registerJarCheck(ctx: Context) {
		val jar = ctx.extension.jarTask.flatMap { tasks.named<Jar>(it) }
		val verify = tasks.register("checkModJar") {
			group = "verification"
			description = "Checks that the release jar contains the mod, resources, and loader metadata"
			inputs.file(jar.flatMap { it.archiveFile })
			doLast {
				ZipFile(jar.get().archiveFile.get().asFile).use { zip ->
					val platformPath = "dev/obscuria/elixirum/platform"
					val entrypoint = when (ctx.loader) { Loader.Fabric -> "Fabric"; Loader.NeoForge -> "NeoForge"; else -> "Forge" }
					check(zip.getEntry("$platformPath/${ctx.loader.id}/${entrypoint}ArsElixirum.class") != null) { "Missing platform entrypoint from ${zip.name}" }
					for (loader in listOf("fabric", "forge", "neoforge").filter { it != ctx.loader.id }) {
						check(zip.entries().asSequence().none { it.name.startsWith("$platformPath/$loader/") && it.name.endsWith(".class") }) { "Foreign platform $loader classes in ${zip.name}" }
						check(zip.getEntry("elixirum.$loader.mixins.json") == null) { "Foreign platform $loader mixins in ${zip.name}" }
					}
					val expected = listOf("dev/obscuria/elixirum/ArsElixirum.class", "elixirum.png",
						"elixirum.mixins.json", "elixirum.${ctx.loader.id}.mixins.json", ctx.loader.modManifestPath)
					expected.forEach { check(zip.getEntry(it) != null) { "Missing $it from ${zip.name}" } }
					for (name in listOf("elixirum.mixins.json", "elixirum.${ctx.loader.id}.mixins.json")) {
						val config = zip.getInputStream(zip.getEntry(name)).bufferedReader().use { JSON.parseToJsonElement(it.readText()).jsonObject }
						val refmap = config["refmap"]?.jsonPrimitive?.content
						if (refmap != null) check(zip.getEntry(refmap) != null) { "Missing $refmap from ${zip.name}" }
						for (side in listOf("mixins", "client", "server")) for (mixin in config.getValue(side).jsonArray) {
							val path = (config.getValue("package").jsonPrimitive.content + "." + mixin.jsonPrimitive.content).replace('.', '/') + ".class"
							check(zip.getEntry(path) != null) { "Missing mixin $path from ${zip.name}" }
						}
					}
					val metadata = zip.getInputStream(zip.getEntry(ctx.loader.modManifestPath)).bufferedReader().use { it.readText() }
					check(!metadata.contains("\${")) { "Unexpanded metadata in ${zip.name}" }
					if (ctx.loader is Loader.Fabric) {
						val entrypoints = JSON.parseToJsonElement(metadata).jsonObject.getValue("entrypoints").jsonObject
						for (entries in entrypoints.values) for (entry in entries.jsonArray) {
							val path = entry.jsonPrimitive.content.replace('.', '/') + ".class"
							check(zip.getEntry(path) != null) { "Missing entrypoint $path from ${zip.name}" }
						}
						if (ctx.currentMcVersion == "1.20.1") check(zip.getEntry("elixirum.accesswidener") != null)
					} else if (ctx.loader is Loader.Forge) check(zip.getEntry("META-INF/accesstransformer.cfg") != null)
					if (ctx.currentMcVersion == "1.21.1") {
						val song = zip.getInputStream(zip.getEntry("data/elixirum/jukebox_song/wunschpunsch.json")).bufferedReader().use { JSON.parseToJsonElement(it.readText()).jsonObject }
						check(song.getValue("sound_event").jsonPrimitive.content == "elixirum:record.wunschpunsch") { "Jukebox song must reference the registered sound event, not the audio file" }
						val recipe = zip.getInputStream(zip.getEntry("data/elixirum/recipe/alchemist_eye.json")).bufferedReader().use { JSON.parseToJsonElement(it.readText()).jsonObject }
						check(recipe.getValue("result").jsonObject.getValue("id").jsonPrimitive.content == "elixirum:alchemist_eye")
						check(zip.getEntry("data/elixirum/tags/block/heat_source.json") != null)
						check(zip.getEntry("data/elixirum/loot_table/blocks/potion_shelf.json") != null)
						check(zip.getEntry("dev/obscuria/elixirum/compat/archivist/codex/Codex.class") != null)
					}
				}
			}
		}
		tasks.named("check") { dependsOn(verify) }
	}

	private fun Project.registerBuildAndCollectTask(ctx: Context) {
		tasks.register<Copy>("buildAndCollect") {
			from(
				tasks.named(ctx.extension.jarTask.get()),
				tasks.named("liteJar"),
				tasks.named(ctx.extension.sourcesJarTask.get()),
				tasks.named("javadocJar")
			)
			into(rootProject.layout.buildDirectory.file("libs/${ctx.basicVersion}"))
			dependsOn("build")
			group = "build"
		}
	}
}
