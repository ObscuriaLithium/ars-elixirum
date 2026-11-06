@file:Suppress("unused", "DuplicatedCode")

import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import kotlinx.serialization.json.Json
import me.modmuss50.mpp.ReleaseType
import net.peanuuutz.tomlkt.Toml
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.internal.extensions.stdlib.toDefaultLowerCase
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.maven
import java.util.*

val Project.sc: StonecutterBuildExtension
	get() = extensions.getByType<StonecutterBuildExtension>()

fun Project.prop(name: String): String = (project.sc.properties.get<String>(name))

fun Project.env(variable: String): String? {
	providers.environmentVariable(variable).orNull?.let { return it }
	return rootProject.file(".env").takeIf { it.exists() }?.let { f ->
		Properties().apply { f.inputStream().use(::load) }.getProperty(variable)
	}
}

fun Project.versionProp(name: String): String = prop(name).substringBefore('+')

fun Project.envTrue(variable: String): Boolean = env(variable)?.toDefaultLowerCase() == "true"

fun releaseTypeFromChannelTag(channelTag: String): ReleaseType =
	ReleaseType.of(channelTag.substringAfter('-').substringBefore('.').ifEmpty { "stable" })

fun RepositoryHandler.strictMaven(
	url: String, vararg groups: String, configure: MavenArtifactRepository.() -> Unit = {}
) = exclusiveContent {
	forRepository { maven(url) { configure() } }
	filter { groups.forEach(::includeGroup) }
}

fun Project.extractChangelogSection(version: String): String {
	val text = rootProject.file("CHANGELOG.md").readText()
	val heading = Regex("""^##\s*\[?${Regex.escape(version)}]?.*$""", RegexOption.MULTILINE)
	val match = heading.find(text) ?: error("CHANGELOG.md has no '## [$version]' section — add one before releasing.")
	val start = match.range.last + 1
	val end = Regex("""^##\s""", RegexOption.MULTILINE).find(text, start)?.range?.first ?: text.length
	return text.substring(start, end).trim()
}

val JSON = Json { prettyPrint = true; encodeDefaults = true; explicitNulls = false }
val TOML = Toml { }
