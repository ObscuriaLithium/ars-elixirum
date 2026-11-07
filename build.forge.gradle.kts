plugins {
	id("net.neoforged.moddev.legacyforge")
	id("mod-platform")
}

platform.dependencies {
	required("minecraft") { forgeLikeVersionRange = "[${sc.current.version}]" }
	required("forge") { forgeLikeVersionRange = "[${prop("deps.forge")},)" }
	bundled("mixinextras") { forgeLikeVersionRange = "[0.5.0,)" }
}

legacyForge {
	version = "${sc.current.version}-${prop("deps.forge")}"
	validateAccessTransformers = true
	accessTransformers.from(rootProject.file("src/main/resources/META-INF/accesstransformer.cfg"))
	parchment {
		val (minecraft, mappings) = prop("deps.parchment").split(':')
		minecraftVersion = minecraft
		mappingsVersion = mappings
	}
	runs {
		register("client") { client() }
		register("server") { server() }
		register("data") {
			data()
			programArguments.addAll("--mod", prop("mod.id"), "--all", "--output",
				rootProject.file("forge/src/generated/resources").absolutePath,
				"--existing", rootProject.file("src/main/resources").absolutePath)
		}
	}
	mods { register(prop("mod.id")) { sourceSet(sourceSets.main.get()) } }
}

mixin {
	add(sourceSets.main.get(), "${prop("mod.id")}.refmap.json")
	config("${prop("mod.id")}.mixins.json")
	config("${prop("mod.id")}.forge.mixins.json")
}

dependencies {
	annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
	compileOnly("io.github.llamalad7:mixinextras-common:0.5.0")
	annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.0")
	implementation("io.github.llamalad7:mixinextras-forge:0.5.0")
	jarJar("io.github.llamalad7:mixinextras-forge:0.5.0")
	modImplementation("maven.modrinth:fragmentum:${prop("deps.fragmentum-artifact")}")
	modImplementation("maven.modrinth:archivist-api:${prop("deps.archivist-artifact")}")
}

tasks.named<Jar>("jar") { finalizedBy("reobfJar") }
tasks.named("createMinecraftArtifacts") { dependsOn("stonecutterGenerate") }
