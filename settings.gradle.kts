pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "PulseDesktop"

include(":mvi")
include(":navigation")
include(":architecture")
include(":demo:app")
include(":demo:feature:count")
