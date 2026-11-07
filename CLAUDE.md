# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

PulsePlatform is a rule set and a library for Compose UIs that AI agents build, on the desktop and
on Android and iOS. The rules (the `architecture` artifact) are Konsist assertions an app runs from
its own tests, and their failure messages say what to move where; the library (`mvi-desktop`, `mvi-mobile`,
`navigation-desktop`, `navigation-mobile`) holds the MVI pieces those rules expect. The docs present the rules first and the
library second.

The library is Kotlin MVI (Model-View-Intent). Its desktop artifacts
publish a JVM target only. Android and iOS get a separate artifact, `mvi-mobile`, a Kotlin
Multiplatform module (Android, `iosArm64`, `iosSimulatorArm64`) that keeps `PulseState`, `PulseAction`, `PulseEvent`, `PulseViewModel<State, Action, Event>` and
`PulseContent` in the same package and leaves out the Container, `PulseHost`, Broadcast and Unicast,
since a phone screen is one ViewModel.

### Purpose

The core artifact provides the MVI building blocks — the `PulseState`, `PulseAction`, `PulseEvent`,
`PulseBroadcast` and `PulseUnicast` marker interfaces, the `PulseViewModel` and `PulseContainer` base
classes, and the `PulseHost` and `PulseContent` composables.

`PulseViewModel` and `PulseContainer` extend `androidx.lifecycle.ViewModel`, so whichever
`ViewModelStore` holds them decides how long they live. `PulseContent` calls `setupOnce()`, which
runs `onSetup()` the first time an instance is observed and never again. The `navigation-desktop`
artifact adds `rememberPulseViewModel` / `rememberPulseContainer`, and
`rememberPulseNavEntryDecorators()` scopes an instance to a Navigation 3 back stack entry.

The `architecture` artifact publishes the conventions themselves as Konsist assertions, so an app
can check in its own test suite that it uses PulsePlatform the way the library expects. Among them is the
view layout the demo follows: a feature's root holds the **host** (a file named `…Host`), which
creates the ViewModels and the Container and lays out **contents**, beside the Container. A content,
under `content/<name>`, binds one ViewModel with `PulseContent`, and its ViewModel sits beside it.
**Sections** and **components** are pure composables that take data and callbacks only, at the
feature root or inside one content; only their role differs. Packages decide which is which —
`content`, `section` and `component`, the only packages under a feature's root, so a pure helper such
as a label goes in `component` — and names are free except the host's. States and messages
live in a `…Contracts.kt` file beside the ViewModel or Container that uses them. Every section and
component has a `@Preview` named `<Name>_Preview`, or `<Name>_Preview_<Condition>`
for the state it shows, such as `PulseTextButton_Preview_Disabled`. `assertPulseMobileConventions` checks an app on `mvi-mobile` instead: a feature is one
`…Screen` that binds the one ViewModel beside it with `PulseContent`, with only `section` and
`component` under it. The checks match the Pulse types by fully qualified name, so `architecture`
depends on neither `mvi-desktop` nor `mvi-mobile`. `assertPulseModuleConventions` checks a multi-module app laid out like Now in Android (`app`,
`feature/<name>`, `core/<name>`) with Clean Architecture's dependency direction: `core/domain` holds
the entities (`model`), repository interfaces (`repository`) and any use cases (`usecase`) and depends
on nothing, `core/data` implements the interfaces (`repository`) over data sources (`local`) and what
they store (`model`), `core/designsystem` depends on nothing so its building blocks take plain values and sit in a package per kind (`button`, `card`, `theme`),
`core/ui` draws the domain's entities, and a feature reaches data only through the domain.
Dependencies are read from imports.
The module names are a `PulseModuleLayout`, which a project with names of its own replaces.
`assertPulseKoinConventions` uses the same layout: a Koin module (`org.koin.dsl.module` or Koin
Annotations' `@Module`) is declared only in the app module, where the whole graph can be read;
resolving from Koin is allowed anywhere.
`assertPulseDomainConventions` checks how a repository (a class or interface named `…Repository`)
hands out data: a value to follow is a cold `Flow` or a `StateFlow`, and a function that fetches a
value once returns kotlin-result's `Result<V, E>`. Commands that return nothing are not checked, and a
repository may hold other repositories and the functions a data module exposes. A ViewModel may call a
repository directly, so a use case (a class named `…UseCase`) is written only when it has work of its
own: it lives in `usecase`, and no public function of it is a single line.
`assertPulseResourceConventions` keeps an app translatable: every module that draws keeps its text in
its own `composeResources` and reads it with `stringResource`, the domain holds no string literal with
a letter in it, and a composable hands no literal to `Text` or to a `text`, `title`, `label`,
`placeholder` or `contentDescription` argument (previews and value-only templates pass). The demo's
text is in `values` (English) and `values-ja` (Japanese).

## Development Commands

### Build and Testing
- **Build the project**: `./gradlew build`
- **Run tests**: `./gradlew allTests`
- **Clean build**: `./gradlew clean`
- **Check code quality**: `./gradlew ktlintCheck` (auto-fix with `./gradlew ktlintFormat`)
- **Test coverage**: `./gradlew koverHtmlReport` (opens at `build/reports/kover/html/index.html`; XML via `koverXmlReport`)
- **Publish to local Maven**: `./gradlew :library:mvi-desktop:publishToMavenLocal :library:navigation-desktop:publishToMavenLocal :library:architecture:publishToMavenLocal :library:mvi-mobile:publishToMavenLocal :library:navigation-mobile:publishToMavenLocal`
- **Run the mobile tests**: `./gradlew :library:mvi-mobile:testDebugUnitTest :library:mvi-mobile:iosSimulatorArm64Test :library:navigation-mobile:testDebugUnitTest :library:navigation-mobile:iosSimulatorArm64Test` (the iOS tests need a Mac; CI runs them in a macOS job)
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
| `library/mvi-desktop/src/jvmTest` | `PulseViewModel` and `PulseContainer`, and the composables through `createComposeRule()` from `ui-test-junit4` |
| `library/navigation-desktop/src/jvmTest` | `rememberPulseViewModel`, `rememberPulseContainer`, `rememberPulseNavEntryDecorators` |
| `library/architecture/src/jvmTest` | The Konsist assertions, over conforming and violating fixtures |
| `library/mvi-mobile/src/commonTest` | The mobile `PulseViewModel`, on Android and iOS |
| `library/mvi-mobile/src/androidUnitTest`, `src/iosTest` | `PulseContent`, under Robolectric and with `runComposeUiTest` |
| `library/navigation-mobile/src/…` | `rememberPulseViewModel` and `rememberPulseNavEntryDecorators` on mobile, laid out like `mvi-mobile`'s tests |

### Coverage

Coverage is enforced rather than reported: `./gradlew koverVerify` fails when lines or instructions
fall below 95%, and the pull request check runs it. Branch coverage is left out of the gate on
purpose — the remaining branches are the `changedInstance` arms the Compose compiler generates for
default parameters, and reaching them takes tests written against the compiler rather than against
the library. A new public declaration is still expected to arrive with the test that covers it.

## Project Structure

```
PulsePlatform/
├── library/                          # Everything published; each module is one artifact
│   ├── mvi-desktop/                  # :library:mvi-desktop — core artifact: mvi-desktop
│   │   └── src/
│   │       ├── jvmMain/kotlin/jp/kaleidot725/pulse/mvi/
│   │       │   ├── PulseState.kt     # Marker interface for state
│   │       │   ├── PulseAction.kt    # Marker interface for actions
│   │       │   ├── PulseEvent.kt     # Marker interface for one-time events
│   │       │   ├── PulseBroadcast.kt # Marker interface for Container to ViewModel messages
│   │       │   ├── PulseUnicast.kt   # Marker interface for ViewModel to Container messages
│   │       │   ├── PulseViewModel.kt # Abstract ViewModel: state, actions, events, unicast
│   │       │   ├── PulseContainer.kt # Coordinates ViewModels: broadcast, refresh, close
│   │       │   └── PulseContent.kt   # PulseHost and PulseContent composables
│   │       └── jvmTest/kotlin/…      # PulseViewModelTest, PulseContainerTest, PulseContentTest
│   ├── navigation-desktop/           # :library:navigation-desktop — optional artifact: navigation-desktop
│   │   └── src/jvmMain/kotlin/jp/kaleidot725/pulse/navigation/
│   │       └── PulseNavigation.kt    # rememberPulseViewModel / rememberPulseContainer /
│   │                                 # rememberPulseNavEntryDecorators
│   ├── architecture/                 # :library:architecture — optional artifact: architecture
│   │   └── src/
│   │       ├── jvmMain/kotlin/jp/kaleidot725/pulse/architecture/
│   │       │   ├── PulseConventions.kt       # assertPulseMviConventions and the checks it runs
│   │       │   ├── PulseDomainConventions.kt # assertPulseDomainConventions: how repositories hand out data
│   │       │   ├── PulseKoinConventions.kt   # assertPulseKoinConventions: Koin modules live in the app
│   │       │   ├── PulseModuleConventions.kt # assertPulseModuleConventions: multi-module layout
│   │       │   ├── PulseModuleLayout.kt      # which module names play which layer
│   │       │   └── PulseResourceConventions.kt # assertPulseResourceConventions: text comes from string resources
│   │       └── jvmTest/…             # the checks, over conforming and violating fixtures
│   ├── mvi-mobile/                   # :library:mvi-mobile — Android and iOS artifact: mvi-mobile
│   │   └── src/
│   │       ├── commonMain/kotlin/jp/kaleidot725/pulse/mvi/   # the same package as mvi: PulseState, PulseAction,
│   │       │                                                 # PulseEvent, PulseViewModel and PulseContent
│   │       ├── commonTest/…          # PulseViewModelTest, on both platforms
│   │       ├── androidUnitTest/…     # PulseContentTest (Robolectric)
│   │       └── iosTest/…             # PulseContentIosTest (runComposeUiTest)
│   └── navigation-mobile/            # :library:navigation-mobile — Android and iOS artifact: navigation-mobile
│       └── src/commonMain/kotlin/jp/kaleidot725/pulse/navigation/
│           └── PulseNavigation.kt    # rememberPulseViewModel / rememberPulseNavEntryDecorators
├── demo/                             # Pulse grid demo app, split the way assertPulseModuleConventions expects
│   ├── app/                          # :demo:app — the window, NavDisplay, the back stack, and di/:
│   │   └── src/jvmMain/kotlin/jp/kaleidot725/pulse/demo/   # every Koin module, started in Main
│   │       ├── DemoApp.kt
│   │       └── Main.kt
│   ├── core/domain/                  # :demo:core:domain — model/ PulseArea, PulseAreaPosition (the neighbour
│   │                                 # rule), repository/ PulseCountRepository, usecase/ ReceivePulseUseCase;
│   │                                 # no Koin
│   ├── core/data/                    # :demo:core:data — repository/ DataStorePulseCountRepository,
│   │                                 # local/ PulseCountLocalDataSource and pulseCountDataStore()
│   ├── core/designsystem/            # :demo:core:designsystem — theme/, card/, button/: one package per kind
│   └── feature/count/                # :demo:feature:count — one feature, host at its root; reaches
│                                     # the counts through the domain's repository, never core/data.
│                                     # It resolves its ViewModels from Koin; rememberPulseViewModel
│                                     # still decides how long they live
│       └── src/jvmMain/kotlin/jp/kaleidot725/pulse/demo/count/
│           ├── PulseCountHost.kt     # Host: creates the ViewModels and Container, lays out contents
│           ├── PulseCountContainer.kt
│           ├── PulseCountContracts.kt # the Container's Broadcast and Unicast
│           ├── component/            # Pure composables shared by the feature
│           └── content/area/         # Content: one ViewModel bound with PulseContent
│               ├── PulseAreaContent.kt
│               ├── PulseAreaViewModel.kt
│               ├── PulseAreaContracts.kt # the ViewModel's State, Action and Event
│               ├── section/          # Pure composables of this content
│               └── component/
│       # Four areas share a Container: an area counts its own tap and announces
│       # it as a Unicast, the Container broadcasts it back to all four, and each
│       # decides what to do — the origin ignores the copy of its own tap
├── docs/                             # VitePress documentation site
├── build.gradle.kts                  # Root build file
├── settings.gradle.kts               # Project settings
├── jitpack.yml                       # JitPack publish config
└── gradle.properties
```

Sources sit in `jvmMain` and tests in `jvmTest`, because `jvm()` is the only target: with no second
platform, `commonMain` would be a layer with nothing on the other side. A module with compose
resources sets `generateResClass = always`, since the plugin generates `Res` on its own only for
resources in `commonMain`.

## Technical Details

- **Language**: Kotlin Multiplatform with a single `jvm()` target
- **Build System**: Gradle 8.14.5 with Kotlin DSL
- **Kotlin Version**: 2.3.10
- **Compose Multiplatform Version**: 1.10.1
- **Coroutines Version**: 1.10.2
- **Lifecycle Version**: 2.10.0
- **Navigation 3 Version**: 1.1.1 (`navigation-desktop` and `navigation-mobile` modules only)
- **Konsist Version**: 0.17.3 (`architecture` module only)
- **Android Gradle Plugin**: 8.13.2, compileSdk 36, minSdk 26 (`mvi-mobile` module only)
- **JVM Toolchain**: Java 17
- **Code Style**: Official Kotlin code style with explicit API mode

## Key Configuration

- Group ID: `com.github.kaleidot725.PulsePlatform` (what JitPack serves for a multi-module repo)
- Artifact IDs: `mvi-desktop`, `navigation-desktop`, `architecture`, `mvi-mobile`, `navigation-mobile` — the projects are `:library:mvi-desktop`,
  `:library:navigation-desktop`, `:library:architecture`, `:library:mvi-mobile` and `:library:navigation-mobile`, and JitPack names an artifact after the project,
  so the `library` directory does not appear in the coordinates
- Packages: `jp.kaleidot725.pulse.mvi`, `jp.kaleidot725.pulse.navigation`,
  `jp.kaleidot725.pulse.architecture` — one package per artifact, all three beside each other under
  `pulse`. The core package and the `Pulse` class prefix are unchanged by the rename
- JitPack dependency: `implementation("com.github.kaleidot725.PulsePlatform:mvi-desktop:<version>")`, and
  `testImplementation("com.github.kaleidot725.PulsePlatform:architecture:<version>")` for the
  convention checks
