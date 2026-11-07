# What is PulseDesktop?

PulseDesktop is a framework for building **Compose Desktop** apps. It decides how a window is split, where each part keeps its state, how the parts talk to each other and how long they live. Your own tests can check that the code keeps to those decisions.

## Why a desktop framework

On a phone, one screen usually maps to one ViewModel. A desktop window is different: a sidebar, an editor and an inspector are on screen together. Each changes on its own, and some of those changes have to reach the others.

Putting all of it in one ViewModel turns that ViewModel into the file that knows everything. Splitting it with no way to coordinate makes the panels reach into each other instead. PulseDesktop gives each part its own ViewModel and puts one Container above them.

## How a window is split

A window is built from four kinds of composable. The package a composable lives in decides which one it is.

| Role | What it does | Where it lives |
|---|---|---|
| **Host** | What a route shows. It creates the ViewModels and the Container, and lays out the contents | a file named `…Host` at the root of a feature package |
| **Content** | The part one ViewModel answers for. It binds that ViewModel with `PulseContent` | `content/<name>`, beside its ViewModel |
| **Section** | A part of the screen drawn from a state and callbacks, previewed on its own | a `section` package |
| **Component** | A small building block. It takes data and callbacks, so it can be reused and previewed on its own | a `component` package |

## How the contents work together

The Container held by the host is what connects the contents. Three features carry the messages between them.

| Feature | Description |
|---|---|
| **Broadcast** | Deliver a typed message from the Container to every content's ViewModel at once |
| **Unicast** | Send a typed message from one content's ViewModel up to the Container |
| **View Refresh** | Rebuild the view tree under the Container without losing any ViewModel's state |

## An example window

In the window below, the editor reports a change with Unicast, and the Container broadcasts it to all three contents. Each content decides on its own how to react.

```mermaid
flowchart TB
    PC["PulseContainer<br/><i>held by the host</i>"]
    subgraph W["Window"]
        direction LR
        A["SideBar content<br/><i>SideBarViewModel</i>"]
        B["Editor content<br/><i>EditorViewModel</i>"]
        C["Inspector content<br/><i>InspectorViewModel</i>"]
    end
    B -- "unicast" --> PC
    PC -- "broadcast" --> A
    PC -- "broadcast" --> B
    PC -- "broadcast" --> C
```

## Artifacts

PulseDesktop is split into four artifacts. Only `mvi` is required, or `mvi-mobile` in an Android or iOS app, which shares its package.

| Artifact | What it decides | Contents |
|---|---|---|
| `mvi` | where state lives and how contents talk | `PulseViewModel`, `PulseContainer`, `PulseHost`, `PulseContent` and the five marker interfaces |
| `navigation` | how long a host's ViewModels live | `rememberPulseViewModel`, `rememberPulseContainer`, `rememberPulseNavEntryDecorators` |
| `architecture` | that the code keeps to the conventions | Konsist assertions for the state rules, the host, content, section and component split, previews, repositories, module dependencies and Koin |
| `mvi-mobile` | the same for one ViewModel on Android and iOS | `PulseViewModel`, `PulseContent` and three marker interfaces, without the Container — see [Mobile](/guide/mobile) |

## Installation

### Repository

Add JitPack to `settings.gradle.kts`.

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Dependencies

Add the dependencies to `build.gradle.kts`. Replace `<version>` with the latest tag from [GitHub Releases](https://github.com/kaleidot725/PulseDesktop/releases).

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.github.kaleidot725.PulseDesktop:mvi:<version>")

    // Optional: scope ViewModels to a Navigation 3 back stack entry
    implementation("com.github.kaleidot725.PulseDesktop:navigation:<version>")

    // Optional: check the conventions in your tests
    testImplementation("com.github.kaleidot725.PulseDesktop:architecture:<version>")
}
```

## Requirements

| Requirement | Version |
|---|---|
| Java | 17+ |
| Kotlin | 2.0+ |
| Compose Multiplatform | 1.6+ |

## Next Steps

- [Getting Started](/guide/getting-started) — build your first counter app
- [Architecture](/guide/architecture) — understand how state flows
- [Container](/guide/container) — make the contents work together
- [Navigation 3](/guide/navigation3) — scope ViewModels to a back stack entry
- [Architecture checks](/guide/architecture-checks) — check in your tests that the code follows the conventions
