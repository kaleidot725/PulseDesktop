<p align="center">
  <img src="docs/public/logo.svg" width="96" alt="PulsePlatform" />
</p>

# PulsePlatform

[![Kotlin](https://img.shields.io/badge/kotlin-2.3.10-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.10.1-blue)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![](https://jitpack.io/v/kaleidot725/PulsePlatform.svg)](https://jitpack.io/#kaleidot725/PulsePlatform)

PulsePlatform is a rule set and a library for Compose UIs that AI agents build. The **rules** are
tests your project runs: they check how each feature is split, how state and messages are declared,
how repositories hand out data, which module depends on which, and where the text comes from. A
failure names the file and says what to move where, so an agent fixes it the way it fixes a compile
error. The **library** holds the pieces the rules expect, so the right structure is also the easy one
to write. It builds desktop apps with Compose Desktop, and Android and iOS apps with Compose
Multiplatform.

Read [What is PulsePlatform?](https://kaleidot725.github.io/PulsePlatform/guide/) for the idea behind it.

![demo](docs/demo.png)

## What you get

Use the rules on every platform, and the MVI artifact for your target. A desktop window shows
several panels at once, so a feature is a **host** that lays out **contents**, each binding its own
ViewModel, with a **Container** carrying messages between them. A phone shows one screen at a time,
so a mobile feature is one **screen** binding one ViewModel. On both, **sections** and
**components** take data and callbacks only.

| Artifact | Role | Platforms |
|---|---|---|
| `architecture` | the rules, as [Konsist](https://docs.konsist.lemonappdev.com/) assertions for your tests | checks desktop and mobile code |
| `mvi` | `PulseViewModel`, `PulseContent`, and the `PulseContainer` and `PulseHost` that let a window's contents work together | desktop |
| `mvi-mobile` | `PulseViewModel` and `PulseContent`, one ViewModel per screen | Android, iOS |
| `navigation` | ties ViewModels and Containers to a Navigation 3 back stack entry | desktop |

## Installation

Add JitPack to `settings.gradle.kts`, then the artifacts for your platform, replacing `<version>`
with a tag from [Releases](https://github.com/kaleidot725/PulsePlatform/releases).

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}

// build.gradle.kts — desktop
dependencies {
    implementation("com.github.kaleidot725.PulsePlatform:mvi:<version>")
    implementation("com.github.kaleidot725.PulsePlatform:navigation:<version>")
    testImplementation("com.github.kaleidot725.PulsePlatform:architecture:<version>")
}

// build.gradle.kts — mobile (Compose Multiplatform)
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.github.kaleidot725.PulsePlatform:mvi-mobile:<version>")
        }
    }
}
dependencies {
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

## The rules

One test runs the rules over the whole project. Desktop apps call `assertPulseMviConventions()`,
mobile apps `assertPulseMobileConventions()`, and the other groups are shared. Point your agent at it
from `CLAUDE.md` or `AGENTS.md`, as
[Building with AI agents](https://kaleidot725.github.io/PulsePlatform/guide/ai-agents) describes.

```kotlin
class PulsePlatformConventionTest {
    @Test
    fun `follows the PulsePlatform conventions`() {
        val scope = Konsist.scopeFromProduction()
        scope.assertPulseMviConventions() // or assertPulseMobileConventions() on mobile
        scope.assertPulseDomainConventions()
        scope.assertPulseModuleConventions()
        scope.assertPulseKoinConventions()
        scope.assertPulseResourceConventions()
    }
}
```

## A screen

On mobile, a screen binds its ViewModel with `PulseContent` and hands the state to a section. The
desktop does the same in each content, under a host. The quick starts walk through both:
[desktop](https://kaleidot725.github.io/PulsePlatform/guide/getting-started) and
[mobile](https://kaleidot725.github.io/PulsePlatform/guide/getting-started-mobile).

```kotlin
class CounterViewModel : PulseViewModel<CounterState, CounterAction, CounterEvent>(
    initialUiState = CounterState(),
) {
    override fun onAction(uiAction: CounterAction) {
        when (uiAction) {
            CounterAction.Increment -> update { copy(count = count + 1) }
        }
    }
}

// counter/CounterScreen.kt
@Composable
fun CounterScreen() {
    val viewModel = viewModel { CounterViewModel() }
    PulseContent(viewModel = viewModel) { state, onAction ->
        CounterSection(count = state.count, onIncrement = { onAction(CounterAction.Increment) })
    }
}
```

## Documentation

**[kaleidot725.github.io/PulsePlatform](https://kaleidot725.github.io/PulsePlatform/)** (English and Japanese).

| Topic | Page |
|---|---|
| The idea | [What is PulsePlatform?](https://kaleidot725.github.io/PulsePlatform/guide/) |
| The rules | [Rules](https://kaleidot725.github.io/PulsePlatform/guide/rules/) |
| AI agents | [Building with AI agents](https://kaleidot725.github.io/PulsePlatform/guide/ai-agents) |
| Desktop | [Host and Container](https://kaleidot725.github.io/PulsePlatform/guide/container), [Navigation 3](https://kaleidot725.github.io/PulsePlatform/guide/navigation3) |
| Mobile | [Screens](https://kaleidot725.github.io/PulsePlatform/guide/mobile) |

## Demo

The `demo` directory is a small multi-module desktop app built the way the rules expect, and it
passes all of them. Four areas share one Container, and a tap on one spreads to the two it shares
an edge with. The counts are kept in a DataStore, so they survive a restart.

| Module | Holds |
|---|---|
| `demo/app` | the window, the back stack, and every Koin module |
| `demo/feature/count` | the host, its content, section and components, the ViewModel, and its text in English and Japanese |
| `demo/core/domain` | the areas and the neighbour rule in `model`, the repository interface in `repository`, and the one use case with work of its own in `usecase` |
| `demo/core/data` | the DataStore-backed repository in `repository`, and the data source in front of DataStore in `local` |
| `demo/core/designsystem` | the theme, the card and the buttons, one package per kind |

```bash
./gradlew :demo:app:run
```

## License

```
Copyright 2026 kaleidot725

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
