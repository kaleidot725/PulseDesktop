<p align="center">
  <img src="docs/public/logo.svg" width="96" alt="PulseDesktop" />
</p>

# PulseDesktop

[![Kotlin](https://img.shields.io/badge/kotlin-2.3.10-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.10.1-blue)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![](https://jitpack.io/v/kaleidot725/PulseDesktop.svg)](https://jitpack.io/#kaleidot725/PulseDesktop)

PulseDesktop is a framework for Compose Desktop apps. A desktop window shows many panels at once, so
PulseDesktop gives each **section** of a screen its own `PulseViewModel` and puts one
`PulseContainer` above them. **Broadcast** reaches every section, **Unicast** sends a message up from
one, and **Refresh** rebuilds the view without losing state.

Around that, it decides how a window is split. A **screen** creates the ViewModels and lays out the
sections, a section binds one ViewModel with `PulseContent`, and a **component** takes data and
callbacks only. Your own tests can check that the code keeps to it.

Read [What is PulseDesktop?](https://kaleidot725.github.io/PulseDesktop/guide/) for the idea behind it.

![demo](docs/demo.png)

## Installation

PulseDesktop publishes three artifacts, one package each. `mvi` is the library; the other two are
optional. `navigation` scopes ViewModels to a Navigation 3 back stack entry, and `architecture` checks
in your own tests that the code follows the conventions.

Add JitPack to `settings.gradle.kts`, then the dependencies to `build.gradle.kts`, replacing
`<version>` with a tag from [Releases](https://github.com/kaleidot725/PulseDesktop/releases).

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}

// build.gradle.kts
dependencies {
    implementation("com.github.kaleidot725.PulseDesktop:mvi:<version>")
    implementation("com.github.kaleidot725.PulseDesktop:navigation:<version>")
    testImplementation("com.github.kaleidot725.PulseDesktop:architecture:<version>")
}
```

## Requirements

| Requirement | Version |
|---|---|
| Java | 17+ |
| Kotlin | 2.0+ |
| Compose Multiplatform | 1.6+ |

## Getting started

A ViewModel handles actions and updates its state. A section binds it to the UI with
`PulseContent`, and the screen creates the ViewModel and places the section. The full walkthrough,
including the Container, is in
[Getting Started](https://kaleidot725.github.io/PulseDesktop/guide/getting-started).

```kotlin
class CounterViewModel : PulseViewModel<CounterState, CounterAction, CounterEvent, CounterBroadcast, CounterUnicast>(
    initialUiState = CounterState(),
) {
    override fun onAction(uiAction: CounterAction) {
        when (uiAction) {
            CounterAction.Increment -> update { copy(count = count + 1) }
        }
    }
}

// counter/section/CounterSection.kt
@Composable
fun CounterSection(viewModel: CounterViewModel) {
    PulseContent(viewModel = viewModel) { state, onAction ->
        Button(onClick = { onAction(CounterAction.Increment) }) {
            Text("${state.count}")
        }
    }
}

// counter/CounterScreen.kt
@Composable
fun CounterScreen() {
    val viewModel = rememberPulseViewModel { CounterViewModel() }

    CounterSection(viewModel = viewModel)
}
```

## Documentation

**[kaleidot725.github.io/PulseDesktop](https://kaleidot725.github.io/PulseDesktop/)** (English and Japanese).

| Artifact | Package | Documentation |
|---|---|---|
| `mvi` | `jp.kaleidot725.pulse.mvi` | [Architecture](https://kaleidot725.github.io/PulseDesktop/guide/architecture) |
| `navigation` | `jp.kaleidot725.pulse.navigation` | [Navigation 3](https://kaleidot725.github.io/PulseDesktop/guide/navigation3) |
| `architecture` | `jp.kaleidot725.pulse.architecture` | [Architecture checks](https://kaleidot725.github.io/PulseDesktop/guide/architecture-checks) |

## Demo

The `demo` directory is a small multi-module app built the way the architecture checks expect, and
it passes all of them. Four areas share one Container, and a tap on one spreads to the two it shares
an edge with. The counts are kept in a DataStore, so they survive a restart.

| Module | Holds |
|---|---|
| `demo/app` | the window, the back stack, and every Koin module |
| `demo/feature/count` | the screen, its sections and ViewModels |
| `demo/core/domain` | the areas, the neighbour rule, the repository interface and the use cases |
| `demo/core/data` | the DataStore-backed repository |
| `demo/core/designsystem` | the theme, the card and the buttons |

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
