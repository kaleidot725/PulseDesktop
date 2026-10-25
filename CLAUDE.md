# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

PulseDesktop is a Kotlin MVI (Model-View-Intent) library for Compose Desktop. It publishes a JVM target
only — Android and iOS support was dropped so the library stays straightforward on the desktop.

### Purpose

The core artifact provides the MVI building blocks — the `PulseState`, `PulseAction`, `PulseEvent`,
`PulseBroadcast` and `PulseUnicast` marker interfaces, the `PulseViewModel` and `PulseContainer` base
classes, and the `PulseHost` and `PulseContent` composables.

`PulseViewModel` and `PulseContainer` extend `androidx.lifecycle.ViewModel`, so whichever
`ViewModelStore` holds them decides how long they live. `PulseContent` calls `setupOnce()`, which
runs `onSetup()` the first time an instance is observed and never again. The `navigation`
artifact adds `rememberPulseViewModel` / `rememberPulseContainer`, and
`rememberPulseNavEntryDecorators()` scopes an instance to a Navigation 3 back stack entry.

The `architecture` artifact publishes the conventions themselves as Konsist assertions, so an app
can check in its own test suite that it uses PulseDesktop the way the library expects. Among them is the
view layout the demo follows: a **screen** at the root of a feature package creates the ViewModels and
lays out **sections**, a section binds one ViewModel with `PulseContent`, and a **component** takes
data and callbacks only. Packages decide which is which — `section` and `component` — and names are
free. `assertPulseModuleConventions` checks a multi-module app laid out like Now in Android (`app`,
`feature/<name>`, `core/<name>`) with Clean Architecture's dependency direction: `core/domain` holds
the entities, use cases and repository interfaces and depends on nothing, `core/data` implements the
interfaces, and a feature reaches data only through the domain. Dependencies are read from imports.
The module names are a `PulseModuleLayout`, which a project with names of its own replaces.
`assertPulseDomainConventions` checks how a repository (a class or interface named `…Repository`)
hands out data: a value to follow is a cold `Flow` or a `StateFlow`, and a function that fetches a
value once returns kotlin-result's `Result<V, E>`. Commands that return nothing are not checked, and a
repository may hold other repositories and the functions a data module exposes.

## Development Commands

### Build and Testing
- **Build the project**: `./gradlew build`
- **Run tests**: `./gradlew allTests`
- **Clean build**: `./gradlew clean`
- **Check code quality**: `./gradlew ktlintCheck` (auto-fix with `./gradlew ktlintFormat`)
- **Test coverage**: `./gradlew koverHtmlReport` (opens at `build/reports/kover/html/index.html`; XML via `koverXmlReport`)
- **Publish to local Maven**: `./gradlew :library:mvi:publishToMavenLocal :library:navigation:publishToMavenLocal :library:architecture:publishToMavenLocal`
- **Run the demo**: `./gradlew :demo:app:run`

## Writing Tests

Name a test case with a backticked sentence, written from the subject's point of view, so a failing
test reads as the promise that broke. Every test class and test case also carries a KDoc line, and it
says what the name cannot: why the behavior matters, with links to the API it covers.

```kotlin
/**
 * Construction stays cheap: whoever owns the instance decides when the work starts, which for a
 * composition is [PulseContent] calling [PulseViewModel.setupOnce].
 */
@Test
fun `does not run onSetup until it is set up`() {
```

### Where a test goes

Put a test in the source set that matches what it needs.

| Source set | Use it for |
|---|---|
| `library/mvi/src/commonTest` | `PulseViewModel` and `PulseContainer` — anything that needs no composition |
| `library/mvi/src/jvmTest` | The composables, through `createComposeRule()` from `ui-test-junit4` |
| `library/navigation/src/jvmTest` | `rememberPulseViewModel`, `rememberPulseContainer`, `rememberPulseNavEntryDecorators` |
| `library/architecture/src/jvmTest` | The Konsist assertions, over conforming and violating fixtures |

### Coverage

Coverage is enforced rather than reported: `./gradlew koverVerify` fails when lines or instructions
fall below 95%, and the pull request check runs it. Branch coverage is left out of the gate on
purpose — the remaining branches are the `changedInstance` arms the Compose compiler generates for
default parameters, and reaching them takes tests written against the compiler rather than against
the library. A new public declaration is still expected to arrive with the test that covers it.

## Project Structure

```
PulseDesktop/
├── library/                          # Everything published; each module is one artifact
│   ├── mvi/                          # :library:mvi — core artifact: mvi
│   │   └── src/
│   │       ├── commonMain/kotlin/jp/kaleidot725/pulse/mvi/
│   │       │   ├── PulseState.kt     # Marker interface for state
│   │       │   ├── PulseAction.kt    # Marker interface for actions
│   │       │   ├── PulseEvent.kt     # Marker interface for one-time events
│   │       │   ├── PulseBroadcast.kt # Marker interface for Container to ViewModel messages
│   │       │   ├── PulseUnicast.kt   # Marker interface for ViewModel to Container messages
│   │       │   ├── PulseViewModel.kt # Abstract ViewModel: state, actions, events, unicast
│   │       │   ├── PulseContainer.kt # Coordinates ViewModels: broadcast, refresh, close
│   │       │   └── PulseContent.kt   # PulseHost and PulseContent composables
│   │       └── commonTest/kotlin/…   # PulseViewModelTest, PulseContainerTest
│   ├── navigation/                   # :library:navigation — optional artifact: navigation
│   │   └── src/commonMain/kotlin/jp/kaleidot725/pulse/navigation/
│   │       └── PulseNavigation.kt    # rememberPulseViewModel / rememberPulseContainer /
│   │                                 # rememberPulseNavEntryDecorators
│   └── architecture/                 # :library:architecture — optional artifact: architecture
│       └── src/
│           ├── jvmMain/kotlin/jp/kaleidot725/pulse/architecture/
│           │   ├── PulseConventions.kt       # assertPulseMviConventions and the checks it runs
│           │   ├── PulseDomainConventions.kt # assertPulseDomainConventions: how repositories hand out data
│           │   ├── PulseModuleConventions.kt # assertPulseModuleConventions: multi-module layout
│           │   └── PulseModuleLayout.kt      # which module names play which layer
│           └── jvmTest/…             # the checks, over conforming and violating fixtures
├── demo/                             # Pulse grid demo app, split the way assertPulseModuleConventions expects
│   ├── app/                          # :demo:app — the window, NavDisplay, the back stack, and the wiring:
│   │   └── src/{commonMain,jvmMain}/kotlin/jp/kaleidot725/pulse/demo/   # creates the DataStore repository
│   │       ├── DemoApp.kt
│   │       └── Main.kt
│   ├── core/domain/                  # :demo:core:domain — PulseArea, PulseAreaPosition (the neighbour rule),
│   │                                 # PulseCountRepository and the use cases; plain Kotlin
│   ├── core/data/                    # :demo:core:data — DataStorePulseCountRepository, counts kept per area
│   ├── core/designsystem/            # :demo:core:designsystem — PulseDesktopTheme, PulseCard, PulseButton
│   └── feature/count/                # :demo:feature:count — one feature, screen at its root; reaches
│                                     # the counts through the domain use cases, never core/data
│       └── src/commonMain/kotlin/jp/kaleidot725/pulse/demo/count/
│           ├── PulseCountHost.kt     # Screen: creates the ViewModels and Container
│           ├── PulseCountContainer.kt
│           ├── component/            # Stateless composables of the screen
│           ├── section/area/         # Section: one ViewModel bound with PulseContent
│           │   ├── PulseAreaContent.kt
│           │   ├── PulseAreaViewModel.kt
│           │   ├── component/        # Stateless composables of the section
│           │   └── state/
│           └── state/
│       # Four areas share a Container: an area counts its own tap and announces
│       # it as a Unicast, the Container broadcasts it back to all four, and each
│       # decides what to do — the origin ignores the copy of its own tap
├── docs/                             # VitePress documentation site
├── build.gradle.kts                  # Root build file
├── settings.gradle.kts               # Project settings
├── jitpack.yml                       # JitPack publish config
└── gradle.properties
```

Sources sit in `commonMain` even though `jvm()` is the only target. Keeping the source set makes
re-adding a target a build-file change rather than a file move. The exception is `architecture`,
whose sources are in `jvmMain` because Konsist publishes a JVM artifact only.

## Technical Details

- **Language**: Kotlin Multiplatform with a single `jvm()` target
- **Build System**: Gradle 8.14.5 with Kotlin DSL
- **Kotlin Version**: 2.3.10
- **Compose Multiplatform Version**: 1.10.1
- **Coroutines Version**: 1.10.2
- **Lifecycle Version**: 2.10.0
- **Navigation 3 Version**: 1.1.1 (`navigation` module only)
- **Konsist Version**: 0.17.3 (`architecture` module only)
- **JVM Toolchain**: Java 17
- **Code Style**: Official Kotlin code style with explicit API mode

## Key Configuration

- Group ID: `com.github.kaleidot725.PulseDesktop` (what JitPack serves for a multi-module repo)
- Artifact IDs: `mvi`, `navigation`, `architecture` — the projects are `:library:mvi`,
  `:library:navigation` and `:library:architecture`, and JitPack names an artifact after the project,
  so the `library` directory does not appear in the coordinates
- Packages: `jp.kaleidot725.pulse.mvi`, `jp.kaleidot725.pulse.navigation`,
  `jp.kaleidot725.pulse.architecture` — one package per artifact, all three beside each other under
  `pulse`. The core package and the `Pulse` class prefix are unchanged by the rename
- JitPack dependency: `implementation("com.github.kaleidot725.PulseDesktop:mvi:<version>")`, and
  `testImplementation("com.github.kaleidot725.PulseDesktop:architecture:<version>")` for the
  convention checks
