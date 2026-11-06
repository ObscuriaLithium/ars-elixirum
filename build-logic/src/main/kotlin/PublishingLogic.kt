@file:Suppress("unused", "DuplicatedCode")

import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import me.modmuss50.mpp.ModPublishExtension
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*

fun Project.configureLocalMavenPublishing(ctx: Context) {
	apply(plugin = "maven-publish")

	val jarTask = ctx.extension.jarTask.flatMap { tasks.named(it) }
	val srcJarTask = ctx.extension.sourcesJarTask.flatMap { tasks.named(it) }
	val javadocJarTask = tasks.named("javadocJar")

	extensions.configure<PublishingExtension>("publishing") {
		publications {
			create<MavenPublication>("modJar") {
				groupId = ctx.modGroup
				artifactId = ctx.modId
				version = ctx.fullVersion

				artifact(jarTask)
				artifact(srcJarTask) { classifier = "sources" }
				artifact(javadocJarTask) { classifier = "javadoc" }

				pom {
					name.set(ctx.modName)
					description.set(ctx.description)
					inceptionYear.set(ctx.inceptionYear)
					url.set(ctx.homepageUrl)
					licenses {
						license {
							name.set(ctx.licenseName)
							url.set(ctx.licenseUrl)
							distribution.set(ctx.licenseDist)
						}
					}
					developers {
						project.sc.properties.raw("mod", "pom", "developers").asList().forEach { devNode ->
							val dev = devNode.asMap()
							developer {
								id.set(dev["id"]?.toString())
								name.set(dev["name"]?.toString())
								url.set(dev["url"]?.toString())
							}
						}
					}
					scm {
						url.set(ctx.sourcesUrl)
						connection.set(ctx.sourcesUrl.replace("https://", "scm:git:git://").removeSuffix("/") + ".git")
						developerConnection.set(
							ctx.sourcesUrl.replace("https://", "scm:git:ssh://git@").removeSuffix("/") + ".git"
						)
					}
				}
			}
		}

		env("PUB_MAVEN_LOCAL_DIR")?.let { dir ->
			repositories {
				maven(rootProject.file(dir)) {
					name = "CustomLocal"
				}
			}
		}
	}

	tasks.withType<org.gradle.api.publish.tasks.GenerateModuleMetadata>().configureEach { isEnabled = false }
}

fun Project.configureMavenCentralPublishing(ctx: Context) {
	apply(plugin = "com.vanniktech.maven.publish")

	env("PUB_SIGNING_KEY")?.let { extensions.extraProperties["signing.key"] = it }
	env("PUB_SIGNING_ID")?.let { extensions.extraProperties["signing.keyId"] = it }
	env("PUB_SIGNING_PASSWORD")?.let { extensions.extraProperties["signing.password"] = it }
	env("PUB_MAVEN_CENTRAL_USERNAME")?.let { extensions.extraProperties["mavenCentralUsername"] = it }
	env("PUB_MAVEN_CENTRAL_PASSWORD")?.let { extensions.extraProperties["mavenCentralPassword"] = it }

	extensions.configure<MavenPublishBaseExtension>("mavenPublishing") {
		configure(KotlinJvm(javadocJar = JavadocJar.None(), sourcesJar = false))

		if (!ctx.isSnapshot || envTrue("PUB_MAVEN_CENTRAL_SNAPSHOTS")) {
			publishToMavenCentral()
		}
		if (env("PUB_SIGNING_KEY") != null) signAllPublications()

		coordinates(ctx.modGroup, ctx.modId, version as String)
		pom {
			name.set(ctx.modName)
			description.set(ctx.description)
			inceptionYear.set(ctx.inceptionYear)
			url.set(ctx.homepageUrl)
			licenses {
				license {
					name.set(ctx.licenseName)
					url.set(ctx.licenseUrl)
					distribution.set(ctx.licenseDist)
				}
			}
			developers {
				project.sc.properties.raw("mod", "pom", "developers").asList().forEach { devNode ->
					val dev = devNode.asMap()
					developer {
						id.set(dev["id"]?.toString())
						name.set(dev["name"]?.toString())
						url.set(dev["url"]?.toString())
					}
				}
			}
			scm {
				url.set(ctx.sourcesUrl)
				connection.set(ctx.sourcesUrl.replace("https://", "scm:git:git://").removeSuffix("/") + ".git")
				developerConnection.set(
					ctx.sourcesUrl.replace("https://", "scm:git:ssh://git@").removeSuffix("/") + ".git"
				)
			}
		}
	}
}

fun Project.configureModPublishing(ctx: Context) {
	val releaseType = releaseTypeFromChannelTag(ctx.channelTag)

	extensions.configure<ModPublishExtension>("publishMods") {
		val mrStaging = envTrue("PUB_MODRINTH_STAGING")
		val modrinthAccessToken = env("PUB_MODRINTH_TOKEN")
		val curseforgeAccessToken = env("PUB_CURSEFORGE_TOKEN")

		val githubEnabled = envTrue("PUB_GITHUB_ENABLE")
		if (envTrue("PUB_DRY_RUN") || !envTrue("PUB_MODS_ENABLE")) {
			dryRun = true
		}

		val jarTask = ctx.extension.jarTask.flatMap { name -> tasks.named(name).map { it as Jar } }
		val srcJarTask = ctx.extension.sourcesJarTask.flatMap { name -> tasks.named(name).map { it as Jar } }

		file.set(jarTask.flatMap(Jar::getArchiveFile))
		additionalFiles.from(
			tasks.named<LiteJarTask>("liteJar").flatMap { it.outputJar },
			srcJarTask.flatMap(Jar::getArchiveFile)
		)
		type = releaseType
		version = ctx.basicVersion
		changelog.set(rootProject.file("CHANGELOG.md").readText())
		modLoaders.add(ctx.loader.id)

		displayName = "${ctx.modName} ${ctx.basicVersion}"

		if (githubEnabled) {
			github {
				accessToken = env("GITHUB_TOKEN")
				parent(rootProject.tasks.named("publishGithub"))
			}

			// The root task creates the release and writes the result file that children read at
			// execution time, so children must run after it. MPP's parent() only copies the
			// result provider without declaring a dependency, which would race under `org.gradle.parallel`.
			project.tasks.named("publishGithub").configure {
				dependsOn(rootProject.tasks.named("publishGithub"))
			}
		}

		modrinth(ctx, ctx.publishAdditionalVersions, mrStaging, modrinthAccessToken)
		if (!mrStaging) curseforge(ctx, ctx.publishAdditionalVersions, curseforgeAccessToken)
	}
}

private fun ModPublishExtension.modrinth(
	ctx: Context, additionalVersions: List<String>, staging: Boolean, accessToken: String?
) = modrinth {
	if (staging) apiEndpoint = "https://staging-api.modrinth.com/v2"

	environment = ctx.environment
	projectId = project.env("PUB_MODRINTH_PROJECT_ID")

	this.accessToken = accessToken
	minecraftVersions.addAll(listOf(ctx.currentMcVersion) + additionalVersions)
	changelog.set(latestChangelogEntry(changelog.get()))

	if (!staging) {
		val platform = this
		project.afterEvaluate {
			val deps = ctx.extension.dependencies
			deps.required.forEach { dep -> whenNotNull(dep.modrinth) { platform.requires(it) } }
			deps.optional.forEach { dep -> whenNotNull(dep.modrinth) { platform.optional(it) } }
			deps.incompatible.forEach { dep -> whenNotNull(dep.modrinth) { platform.incompatible(it) } }
			deps.embeds.forEach { dep -> whenNotNull(dep.modrinth) { platform.embeds(it) } }
		}
	}
}

private fun ModPublishExtension.curseforge(
	ctx: Context, additionalVersions: List<String>, accessToken: String?
) = curseforge {
	projectId = project.env("PUB_CURSEFORGE_PROJECT_ID_${ctx.loader.id.uppercase()}")

	client = ctx.environmentPhysicalClient
	server = ctx.environmentPhysicalServer

	this.accessToken = accessToken
	minecraftVersions.addAll(listOf(ctx.currentMcVersion) + additionalVersions)

	val platform = this
	project.afterEvaluate {
		val deps = ctx.extension.dependencies
		deps.required.forEach { dep -> whenNotNull(dep.curseforge) { platform.requires(it) } }
		deps.optional.forEach { dep -> whenNotNull(dep.curseforge) { platform.optional(it) } }
		deps.incompatible.forEach { dep -> whenNotNull(dep.curseforge) { platform.incompatible(it) } }
		deps.embeds.forEach { dep -> whenNotNull(dep.curseforge) { platform.embeds(it) } }
	}
}

private fun whenNotNull(stringProp: Property<String>, action: (String) -> Unit) {
	if (!stringProp.orNull.isNullOrBlank()) action(stringProp.get())
}

private fun latestChangelogEntry(full: String): String {
	val lines = full.lines()
	val start = lines.indexOfFirst { it.startsWith("## ") }
	if (start < 0) return full.trim()

	val nextOffset = lines.drop(start + 1).indexOfFirst { it.startsWith("## ") }
	val end = if (nextOffset < 0) lines.size else start + 1 + nextOffset

	return lines.subList(start, end).joinToString("\n").trim()
}
