plugins {
    id("fabric-loom") version "1.13.6"
    id("maven-publish")
}

val minecraftVersion = property("deps.minecraft") as String

version = "${property("mod.version")}+$minecraftVersion"
base.archivesName = property("mod.id") as String

val targetJavaVersion = if (stonecutter.eval(stonecutter.current.version, ">=1.20.5")) 21 else 17
val requiredJava = JavaVersion.toVersion(targetJavaVersion)

repositories {
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json") // Useful for interface injection

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1") // Adds names to lambdas - useful for mixins
    }

    runConfigs.all {
        vmArgs("-Dmixin.debug.export=true") // Exports transformed classes for debugging
        runDir = "../../run" // Shares the run directory between versions
    }
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava

    toolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
}

tasks {
    processResources {
        val props = mapOf(
            "version" to project.version,
            "mc" to project.property("deps.minecraft"),

            "modName" to project.property("mod.name"),
            "modId" to project.property("mod.id"),
            "modDescription" to project.property("mod.description"),
            "authors" to project.property("mod.authors"),
            "license" to project.property("mod.license"),
            "homepage" to project.property("mod.homepage"),
            "issues" to project.property("mod.issues"),
            "sources" to project.property("mod.sources"),

            "fl" to project.property("deps.fabric_loader"),
            "fapi" to project.property("deps.fabric_api"),
        )

        inputs.properties(props)

        filesMatching("fabric.mod.json") { expand(props) }

        val mixinJava = "JAVA_$targetJavaVersion"
        filesMatching("*.mixins.json") { expand("java" to mixinJava) }
        exclude("META-INF/neoforge.mods.toml", "META-INF/mods.toml")
        if (stonecutter.eval(stonecutter.current.version, ">=1.21")) {
            exclude("data/*/loot_tables/**")
        } else {
            exclude("data/*/loot_table/**")
        }
    }

    // Builds the version into a shared folder in `build/libs/${mod version}/`
    register<Copy>("buildAndCollect") {
        group = "build"

        from(project.tasks.named("remapJar"), project.tasks.named("sourcesJar"))
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}
