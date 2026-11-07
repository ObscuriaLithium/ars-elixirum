plugins {
	id("net.neoforged.moddev")
	id("mod-platform")
}

platform.dependencies {
	required("minecraft") { forgeLikeVersionRange = "[${sc.current.version}]" }
	required("neoforge") { forgeLikeVersionRange = "[${prop("deps.neoforge")},)" }
}

neoForge {
	version = prop("deps.neoforge")
	runs {
		register("client") { client() }
		register("server") { server() }
	}
	mods { register(prop("mod.id")) { sourceSet(sourceSets.main.get()) } }
}

dependencies {
	implementation("maven.modrinth:fragmentum:${prop("deps.fragmentum-artifact")}")
}

tasks.named("createMinecraftArtifacts") { dependsOn("stonecutterGenerate") }
