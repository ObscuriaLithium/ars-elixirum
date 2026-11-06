pluginManagement {
    repositories {
        mavenLocal()
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net/") { name = "Fabric" }
		maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
		maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
		maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
		maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "dev.kikugie.loom-back-compat") {
                useModule("dev.kikugie:loomx:${requested.version}")
            }
        }
    }

    includeBuild("build-logic")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.kikugie.stonecutter") version "0.9.7"
    id("dev.kikugie.loom-back-compat") version "0.4.1"
}

rootProject.name = "Ars Elixirum"


stonecutter {
    create(rootProject) {
        fun match(version: String, vararg loaders: String) =
            loaders.forEach {
                version("$version-$it", version).buildscript = "build.$it.gradle.kts"
            }

        match("1.20.1", "fabric", "forge")
        match("1.21.1", "fabric", "neoforge")

        vcsVersion = "1.20.1-forge"
    }
}
