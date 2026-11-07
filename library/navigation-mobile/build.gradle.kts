plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("com.android.library")
    id("org.jetbrains.kotlinx.kover")
    `maven-publish`
}

// JitPack publishes multi-module builds under com.github.<user>.<repo> and versions them by tag, and
// names the artifact after the project: this one is navigation-mobile.
group = "com.github.kaleidot725.PulsePlatform"
version = System.getenv("VERSION") ?: "1.0.0"

kotlin {
    explicitApi()
    jvmToolchain(17)

    androidTarget {
        publishLibraryVariants("release")
    }
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(project(":library:mvi-mobile"))
            api("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
            api("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-navigation3:2.10.0")
            api("org.jetbrains.androidx.navigation3:navigation3-ui:1.1.1")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
        }
        androidUnitTest.dependencies {
            implementation("org.robolectric:robolectric:4.16")
            implementation("androidx.compose.ui:ui-test-junit4:1.10.0")
            implementation("androidx.compose.ui:ui-test-manifest:1.10.0")
        }
        iosTest.dependencies {
            implementation("org.jetbrains.compose.ui:ui-test:1.10.1")
        }
    }
}

android {
    // The same package as the desktop artifact.
    namespace = "jp.kaleidot725.pulse.navigation"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        pom {
            name.set("PulsePlatform Navigation for mobile")
            description.set("Scopes PulsePlatform mobile ViewModels to a Navigation 3 back stack entry")
            url.set("https://github.com/kaleidot725/PulsePlatform")

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
