plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlinx.kover")
    `maven-publish`
}

// JitPack publishes multi-module builds under com.github.<user>.<repo> and versions them by tag.
// Matching both here keeps the inter-module dependencies in the published POMs resolvable.
group = "com.github.kaleidot725.PulseDesktop"
version = System.getenv("VERSION") ?: "1.0.0"

repositories {
    google()
    mavenCentral()
}

kotlin {
    explicitApi()
    jvm()
    jvmToolchain(17)

    sourceSets {
        // Konsist publishes a JVM artifact only, so the assertions cannot live in commonMain.
        val jvmMain by getting {
            dependencies {
                api(project(":library:mvi"))
                api("com.lemonappdev:konsist:0.17.3")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

publishing {
    publications.withType<MavenPublication>().configureEach {

        pom {
            name.set("PulseDesktop architecture checks")
            description.set("Konsist assertions that check an app follows the PulseDesktop conventions")
            url.set("https://github.com/kaleidot725/PulseDesktop")

            licenses {
                license {
                    name.set("Apache License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0")
                }
            }

            developers {
                developer {
                    id.set("kaleidot725")
                    name.set("kaleidot725")
                }
            }
        }
    }
}
