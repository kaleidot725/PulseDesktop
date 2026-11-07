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

rootProject.name = "PulsePlatform"

include(":library:mvi")
include(":library:navigation")
include(":library:architecture")
include(":library:mvi-mobile")
include(":demo:app")
include(":demo:feature:count")
include(":demo:core:domain")
include(":demo:core:data")
include(":demo:core:designsystem")
