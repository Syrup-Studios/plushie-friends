plugins {
    id("net.neoforged.moddev") version "2.0.141"
    id("maven-publish")
}

val minecraftVersion = property("deps.minecraft") as String
val neoForgeVersion = property("deps.neoforge") as String

version = "${property("mod.version")}+$minecraftVersion-neoforge"
group = property("mod.group") as String
base.archivesName = property("mod.id") as String

neoForge {
    version = neoForgeVersion

    runs {
        create("client") {
            client()
            gameDirectory = rootProject.file("run")
        }
        create("server") {
            server()
            gameDirectory = rootProject.file("run")
            programArgument("--nogui")
        }
    }

    mods.create(property("mod.id") as String) {
        sourceSet(sourceSets.main.get())
    }
}

java {
    withSourcesJar()
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.processResources {
    val props = mapOf(
        "version" to project.version,
        "mc" to minecraftVersion,
        "neoforge" to neoForgeVersion,
        "modName" to project.property("mod.name"),
        "modId" to project.property("mod.id"),
        "modDescription" to project.property("mod.description"),
        "authors" to project.property("mod.authors"),
        "license" to project.property("mod.license"),
        "homepage" to project.property("mod.homepage"),
        "issues" to project.property("mod.issues"),
        "sources" to project.property("mod.sources")
    )

    inputs.properties(props)
    filesMatching("META-INF/neoforge.mods.toml") { expand(props) }
    filesMatching("*.mixins.json") { expand("java" to "JAVA_21") }
    exclude("fabric.mod.json", "META-INF/mods.toml", "data/*/loot_tables/**")
}

tasks.named("createMinecraftArtifacts") {
    dependsOn("stonecutterGenerate")
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.named("jar"), tasks.named("sourcesJar"))
    into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    dependsOn("build")
}
