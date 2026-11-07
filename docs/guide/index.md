# What is PulsePlatform?

PulsePlatform is a rule set and a library for building Compose UIs, made to be used by AI agents as much as by people. The rules are tests your project runs. The library holds the pieces those rules expect. Together they build desktop apps with Compose Desktop, and Android and iOS apps with Compose Multiplatform.

## Why rules first

An AI agent writes a screen that compiles and works on the first try. What it does not keep by itself is the shape of the code around it. One screen binds its ViewModel in a section, another keeps a `var` beside its state, a third puts a label in the domain. Each choice works on its own, and the codebase drifts.

A rule written in a prompt or a style guide is easy to skip. A rule that is a failing test is not. PulsePlatform's rules are [Konsist](https://docs.konsist.lemonappdev.com/) assertions you call from one test in your own suite. When the code drifts, the test fails, names the file, and says what to move where. An agent reads that message and fixes the code the same way it fixes a compile error.

The library is the other half. The rules ask that one place binds each ViewModel, that sections and components take only data and callbacks, and that state is one immutable value. The library makes that the easy way to write a screen, so following the rules does not mean writing more code.

## What the rules cover

| Area | What is checked |
|---|---|
| [UI structure](/guide/rules/ui) | how a feature is split into the place that binds the ViewModel, sections and components; states and messages; previews |
| [Domain and data](/guide/rules/domain) | how a repository hands out data, and when a use case is worth writing |
| [Modules and Koin](/guide/rules/modules) | which module may depend on which, the packages inside them, and where Koin modules are declared |
| [Text](/guide/rules/text) | that every string a user reads comes from string resources, so the app can be translated |

## Desktop and mobile

The rules and the core are the same on every platform: `PulseViewModel` holds one immutable state, handles actions and emits events, and `PulseContent` binds it to the UI. What differs is how many ViewModels a screen has.

A desktop window shows a sidebar, an editor and an inspector at once. Each is a **content** with its own ViewModel, a **host** lays them out, and a **Container** above them carries messages between them. A phone shows one screen at a time, so a **screen** has one ViewModel and binds it itself, with no Container.

| | Desktop | Mobile |
|---|---|---|
| Artifact | `mvi` | `mvi-mobile` |
| Targets | JVM (Compose Desktop) | Android, iOS (Compose Multiplatform) |
| A feature is | a host, its contents, and a Container | one screen and one ViewModel |
| Binds a ViewModel | each content | the screen |
| Messages between ViewModels | Broadcast and Unicast through the Container | none |
| Rules | `assertPulseMviConventions()` | `assertPulseMobileConventions()` |

```
Desktop                                  Mobile
counter/                                 counter/
├── CounterHost.kt                       ├── CounterScreen.kt
├── CounterContainer.kt                  ├── CounterViewModel.kt
├── CounterContracts.kt                  ├── CounterContracts.kt
├── content/                             ├── section/
│   └── total/                           └── component/
│       ├── TotalContent.kt
│       ├── TotalViewModel.kt
│       ├── TotalContracts.kt
│       └── section/
├── section/
└── component/
```

## Artifacts

PulsePlatform is published on JitPack as five artifacts. Use the rules on every platform, and the MVI and Navigation 3 artifacts for your target.

| Artifact | Role | Platforms |
|---|---|---|
| `architecture` | the rules, as Konsist assertions for your tests | runs on the JVM; checks desktop and mobile code |
| `mvi` | `PulseViewModel`, `PulseContainer`, `PulseHost`, `PulseContent` and the five marker interfaces | desktop |
| `mvi-mobile` | `PulseViewModel`, `PulseContent` and three marker interfaces | Android, iOS |
| `navigation` | ties ViewModels and Containers to a Navigation 3 back stack entry | desktop |
| `navigation-mobile` | ties ViewModels to a Navigation 3 back stack entry | Android, iOS |

## Installation

Add JitPack to `settings.gradle.kts`, then the artifacts for your platform to `build.gradle.kts`. Replace `<version>` with a tag from [GitHub Releases](https://github.com/kaleidot725/PulsePlatform/releases).

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

```kotlin
// build.gradle.kts — desktop
dependencies {
    implementation("com.github.kaleidot725.PulsePlatform:mvi:<version>")
    implementation("com.github.kaleidot725.PulsePlatform:navigation:<version>")
    testImplementation("com.github.kaleidot725.PulsePlatform:architecture:<version>")
}
```

```kotlin
// build.gradle.kts — mobile (Compose Multiplatform)
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.github.kaleidot725.PulsePlatform:mvi-mobile:<version>")
            implementation("com.github.kaleidot725.PulsePlatform:navigation-mobile:<version>")
        }
    }
}

dependencies {
    // the rules run in the Android unit tests
    testImplementation("com.github.kaleidot725.PulsePlatform:architecture:<version>")
}
```

## Requirements

| Requirement | Version |
|---|---|
| Java | 17+ |
| Kotlin | 2.0+ |
| Compose Multiplatform | 1.6+ |
| Android | `minSdk` 26 |

## Next steps

- [Desktop quick start](/guide/getting-started) — a counter window with a host, a content and a Container
- [Mobile quick start](/guide/getting-started-mobile) — a counter screen on Android and iOS
- [Building with AI agents](/guide/ai-agents) — set up the rules so an agent checks its own work
- [Rules](/guide/rules/) — every rule and why it exists
