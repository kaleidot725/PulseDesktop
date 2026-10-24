plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
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
        val commonMain by getting {
            dependencies {
                api("org.jetbrains.compose.runtime:runtime:1.10.1")
                api("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel:2.10.0")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
            }
            val jvmTest by getting {
                dependencies {
                    implementation(compose.desktop.currentOs)
                    implementation("org.jetbrains.compose.ui:ui-test-junit4:1.10.1")
                }
            }
        }
    }
}

publishing {
    publications.withType<MavenPublication>().configureEach {

        pom {
            name.set("PulseDesktop MVI")
            description.set("A Kotlin MVI library for Compose Desktop")
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
