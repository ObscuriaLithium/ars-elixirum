import org.gradle.api.Project
import org.gradle.kotlin.dsl.named
import org.gradle.language.jvm.tasks.ProcessResources

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
		}
	}
}

private fun String.format(ctx: Context): String {
	return this.replace("@MODID", ctx.modId)
}
