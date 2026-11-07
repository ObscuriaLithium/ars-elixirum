plugins {
	id("fabric-loom")
	id("mod-platform")
}

platform.dependencies {
	required("minecraft") { fabricLikeVersionRange = "~${sc.current.version}" }
	required("fabricloader") { fabricLikeVersionRange = ">=${prop("deps.fabric-loader")}" }
	required("fabric-api") { slug("fabric-api") }
}

loom {
	if (sc.current.version == "1.20.1") accessWidenerPath = rootProject.file("src/main/resources/elixirum.accesswidener")
	mixin { defaultRefmapName = "${prop("mod.id")}.refmap.json" }
	runs {
		named("client") {
			programArgs("--username", "Dev")
			ideConfigGenerated(true)
		}
		named("server") { ideConfigGenerated(true) }
		register("client2") {
			client()
			programArgs("--username", "Player2")
			runDir("run/client2")
			ideConfigGenerated(true)
		}
	}
}

repositories {
	maven("https://maven.terraformersmc.com/releases/")
	maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
}

dependencies {
	minecraft("com.mojang:minecraft:${sc.current.version}")
	mappings(loom.layered {
		officialMojangMappings()
		if (sc.properties.rawOrNull("deps", "parchment") != null)
			parchment("org.parchmentmc.data:parchment-${prop("deps.parchment")}@zip")
	})
	modImplementation("net.fabricmc:fabric-loader:${prop("deps.fabric-loader")}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${prop("deps.fabric-api")}")
	modImplementation("maven.modrinth:fragmentum:${prop("deps.fragmentum-artifact")}")
	if (sc.current.version == "1.20.1") {
		modImplementation("maven.modrinth:archivist-api:${prop("deps.archivist-artifact")}")
	}
	modRuntimeOnly("fuzs.forgeconfigapiport:forgeconfigapiport-fabric:${prop("deps.forge-config-api-port")}")
	modLocalRuntime("com.terraformersmc:modmenu:${prop("deps.modmenu")}")
}
