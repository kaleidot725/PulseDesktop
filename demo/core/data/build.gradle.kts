plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    jvmToolchain(17)

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":demo:core:domain"))
                implementation("androidx.datastore:datastore-preferences-core:1.3.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation("io.insert-koin:koin-core:4.2.2")
            }
        }
    }
}
