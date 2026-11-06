import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

abstract class LiteJarTask : DefaultTask() {

	@get:InputFile
	@get:PathSensitive(PathSensitivity.NONE)
	abstract val sourceJar: RegularFileProperty

	@get:Input
	abstract val nestedLibrariesPath: Property<String>

	@get:Input
	abstract val manifestPath: Property<String>

	@get:Input
	abstract val manifestContent: Property<String>

	@get:OutputFile
	abstract val outputJar: RegularFileProperty

	@TaskAction
	fun repack() {
		val nestedPrefix = nestedLibrariesPath.get()
		val manifestEntry = manifestPath.get()
		var manifestReplaced = false

		ZipFile(sourceJar.get().asFile).use { source ->
			ZipOutputStream(outputJar.get().asFile.outputStream().buffered()).use { target ->
				for (entry in source.entries()) {
					if (entry.name.startsWith(nestedPrefix)) continue

					target.putNextEntry(ZipEntry(entry.name).apply { time = entry.time })
					if (entry.name == manifestEntry) {
						target.write(manifestContent.get().toByteArray())
						manifestReplaced = true
					} else if (!entry.isDirectory) {
						source.getInputStream(entry).use { it.copyTo(target) }
					}
					target.closeEntry()
				}
			}
		}

		check(manifestReplaced) { "'$manifestEntry' not found in ${sourceJar.get().asFile.name}" }
	}
}

fun Project.registerLiteJarTask(ctx: Context) {
	val releaseJar = ctx.extension.jarTask.flatMap { tasks.named<Jar>(it) }

	val liteJar = tasks.register<LiteJarTask>("liteJar") {
		group = "build"
		description = "Assembles the release jar without nested libraries, declaring them as dependencies instead"
		sourceJar.set(releaseJar.flatMap(Jar::getArchiveFile))
		nestedLibrariesPath.set(ctx.loader.nestedLibrariesPath)
		manifestPath.set(ctx.loader.modManifestPath)
		manifestContent.set(providers.provider { ctx.loader.generateManifest(ctx, includeBundled = true) })
		outputJar.set(layout.buildDirectory.file("libs/${ctx.modId}-${ctx.fullVersion}-lite.jar"))
	}

	tasks.named("assemble") { dependsOn(liteJar) }
}
