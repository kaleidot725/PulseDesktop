plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.compose")
    id("org.jetbrains.kotlinx.kover")
    `maven-publish`
}

// JitPack publishes multi-module builds under com.github.<user>.<repo> and versions them by tag, and
// names the artifact after the project: this one is mvi-android.
group = "com.github.kaleidot725.PulseDesktop"
version = System.getenv("VERSION") ?: "1.0.0"

android {
    // The same package as the desktop artifact, so the architecture checks recognise its classes.
    namespace = "jp.kaleidot725.pulse.mvi"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

kotlin {
    explicitApi()
    jvmToolchain(17)
}

dependencies {
    api("androidx.compose.runtime:runtime:1.10.0")
    api("androidx.lifecycle:lifecycle-viewmodel:2.10.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation("org.robolectric:robolectric:4.16")
    testImplementation("androidx.compose.ui:ui-test-junit4:1.10.0")
    testImplementation("androidx.compose.ui:ui-test-manifest:1.10.0")
}

publishing {
    publications {
        register<MavenPublication>("release") {
            artifactId = "mvi-android"
            afterEvaluate { from(components["release"]) }

            pom {
                name.set("PulseDesktop MVI for Android")
                description.set("A Kotlin MVI library for Jetpack Compose on Android")
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
}
