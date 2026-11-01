plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    jvmToolchain(17)

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":demo:core:domain"))
                api("androidx.datastore:datastore-preferences-core:1.3.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            }
        }
    }
}
