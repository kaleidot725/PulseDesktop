plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}

kotlin {
    jvm()
    jvmToolchain(17)

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":demo:feature:count"))
                implementation(project(":demo:core:data"))
                implementation(project(":demo:core:domain"))
                implementation(project(":demo:core:designsystem"))
                implementation(project(":library:navigation"))
                implementation("org.jetbrains.compose.runtime:runtime:1.10.1")
                implementation("org.jetbrains.compose.components:components-resources:1.10.1")
                implementation("org.jetbrains.androidx.navigation3:navigation3-ui:1.1.1")
                implementation("io.insert-koin:koin-core:4.2.2")
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose.resources {
    packageOfResClass = "jp.kaleidot725.pulse.demo.resources"
    generateResClass = always
}

compose.desktop {
    application {
        mainClass = "jp.kaleidot725.pulse.demo.MainKt"

        nativeDistributions {
            packageName = "PulseDesktopDemo"
            packageVersion = "1.0.0"

            macOS {
                bundleID = "jp.kaleidot725.pulse.demo"
            }
        }
    }
}
