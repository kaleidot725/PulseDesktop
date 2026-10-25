# Architecture checks

Everything on this page lives in the optional `architecture` artifact. The package is `jp.kaleidot725.pulse.architecture`, and every declaration is an extension of Konsist's `KoScope`.

For what the checks mean and why they exist, see the [architecture checks guide](/guide/architecture-checks).

## assertPulseMviConventions

```kotlin
fun KoScope.assertPulseMviConventions()
```

Runs the eight checks that follow, from `assertStatesAreDataTypes` to `assertComponentsHavePreviews`, in order. The first failing check throws, so a run reports one problem at a time.

## assertStatesAreDataTypes

```kotlin
fun KoScope.assertStatesAreDataTypes()
```

Requires every class and object implementing `PulseState` to carry the `data` modifier.

### Failure message

| Declaration | Message |
|---|---|
| class | Add `data` so equal states compare equal and Compose can skip the recomposition |
| object | Add `data` so the object prints and compares as a state rather than as an identity |

## assertMessagesAreSealedOrDataTypes

```kotlin
fun KoScope.assertMessagesAreSealedOrDataTypes()
```

Requires the type that implements `PulseAction`, `PulseEvent`, `PulseBroadcast` or `PulseUnicast` to be a `sealed class`, and every case inside it to be a `data class` or `data object`. A case may be a `sealed class` of its own, and its cases are held to the same rule. A message with a single case may instead be one `data class` or `data object` that implements the marker directly. An interface, including a sealed interface, or a class or object without `data` implementing a marker directly is reported.

## assertViewModelsAndContainersAreNamedAfterTheirBase

```kotlin
fun KoScope.assertViewModelsAndContainersAreNamedAfterTheirBase()
```

Requires a `PulseViewModel` subclass to have a name ending in `ViewModel`, and a `PulseContainer` subclass to have a name ending in `Container`.

## assertViewModelsHoldTheirDataInTheirState

```kotlin
fun KoScope.assertViewModelsHoldTheirDataInTheirState()
```

Requires every `PulseViewModel` and `PulseContainer` subclass to declare no `var` property.

## assertSectionsBindOneViewModel

```kotlin
fun KoScope.assertSectionsBindOneViewModel()
```

Requires every file with a composable under a `section` package to call `PulseContent`. The check is per file, so the stateless overload and the `@Preview` beside a section are fine.

## assertComponentsAreStateless

```kotlin
fun KoScope.assertComponentsAreStateless()
```

Requires every composable under a `component` package to take no parameter whose type name ends in `ViewModel` or `Container`, and the file to call neither `PulseContent` nor `PulseHost`.

## assertScreensComposeSections

```kotlin
fun KoScope.assertScreensComposeSections()
```

Requires every file with a composable outside both a `section` and a `component` package — a screen — not to call `PulseContent`.

## assertComponentsHavePreviews

```kotlin
fun KoScope.assertComponentsHavePreviews()
```

Requires every public composable under a `component` package to have a function annotated `@Preview` — from any package, or a multipreview annotation whose name starts with `Preview` — named `<Component>_Preview` or `<Component>_Preview_<Condition>`. Previews are looked for across the whole scope. Private composables and the previews themselves are skipped.

## assertPulseDomainConventions

```kotlin
fun KoScope.assertPulseDomainConventions()
```

Runs the two repository checks below. A repository is a class or an interface whose name ends with `Repository`, and classes and interfaces are checked in one pass, so a failure names every offender at once.

## assertRepositoriesExposeFlowsOrStateFlows

```kotlin
fun KoScope.assertRepositoriesExposeFlowsOrStateFlows()
```

Requires every public property of a repository to be typed as `Flow` or `StateFlow`. A property typed as a repository or as a function is exempt, and a property with no written type fails.

## assertRepositoriesReturnResultsFromOneShotCalls

```kotlin
fun KoScope.assertRepositoriesReturnResultsFromOneShotCalls()
```

Requires every public function of a repository that returns a value to return a `Flow`, a `StateFlow`, or `com.github.michaelbull.result.Result`, recognised by its import or a fully qualified name. A function returning `Unit`, or with a block body and no written type, is a command and is not checked. An expression body with no written type fails.

## assertPulseModuleConventions

```kotlin
fun KoScope.assertPulseModuleConventions(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid)
```

Runs every module check below with the same layout. Dependencies are the modules a file imports from, found by mapping each package to the module that declares it. Files in a module no layer claims are not checked.

| Assertion | Requires |
|---|---|
| `assertNothingDependsOnTheApp(layout)` | no module imports from the app |
| `assertFeaturesDoNotDependOnEachOther(layout)` | a feature imports from no other feature |
| `assertCoreDoesNotDependOnFeatures(layout)` | no module outside the app and the features imports from a feature |
| `assertDomainDependsOnNothing(layout)` | the domain imports only from support modules |
| `assertDataDependsOnlyOnTheDomain(layout)` | the data layer imports only from the domain and support modules |
| `assertSharedUiDependsOnlyOnTheDomain(layout)` | the shared UI imports only from the domain, itself and support modules |
| `assertFeaturesReachDataThroughTheDomain(layout)` | a feature imports nothing from the data layer |
| `assertDomainIsFrameworkFree(layout)` | the domain imports nothing from `androidx.compose`, `androidx.lifecycle` or `jp.kaleidot725.pulse` |

## PulseModuleLayout

```kotlin
data class PulseModuleLayout(
    val app: List<String> = listOf("app"),
    val features: List<String> = listOf("feature/*", "feature/*/api", "feature/*/impl"),
    val domain: List<String> = listOf("core/domain"),
    val data: List<String> = listOf("core/data"),
    val sharedUi: List<String> = listOf("core/designsystem", "core/ui"),
    val support: List<String> = listOf("core/*"),
) {
    companion object {
        val NowInAndroid: PulseModuleLayout
    }
}
```

Maps module directories to layers. Each pattern is matched against the end of a module's directory, the one above `src`, with `*` standing for exactly one directory. Layers are tried in the order declared, so the first that claims a module wins. A trailing `api` or `impl` directory belongs to the feature above it. `NowInAndroid` is the default.

## pulseModule

```kotlin
fun KoFileDeclaration.pulseModule(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid): String?
```

Names the module a file belongs to as the part of its directory a pattern matched — `core/data` or `feature/counter` — or returns null when no layer claims it.

## assertPulseKoinConventions

```kotlin
fun KoScope.assertPulseKoinConventions(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid)
```

Runs the Koin check below. A project without Koin passes.

## assertKoinModulesLiveInTheApp

```kotlin
fun KoScope.assertKoinModulesLiveInTheApp(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid)
```

Requires every file that imports `org.koin.dsl.module` or `org.koin.core.annotation.Module` to be in the app layer of the layout. Files in a module no layer claims are not checked.

## Queries

The queries are the building blocks of the assertions above, and of rules a project writes itself. The type queries return the declarations that extend or implement the matching type directly; the view queries return files, since a role belongs to a file's package rather than to one function.

| Query | Returns |
|---|---|
| `pulseViewModels()` | `List<KoClassDeclaration>` — classes extending `PulseViewModel` |
| `pulseContainers()` | `List<KoClassDeclaration>` — classes extending `PulseContainer` |
| `pulseStateClasses()` | `List<KoClassDeclaration>` — classes implementing `PulseState` |
| `pulseStateObjects()` | `List<KoObjectDeclaration>` — objects implementing `PulseState` |
| `pulseMessageInterfaces()` | `List<KoInterfaceDeclaration>` — interfaces implementing one of the four message markers |
| `pulseMessageClasses()` | `List<KoClassDeclaration>` — classes implementing one of the four message markers |
| `pulseMessageObjects()` | `List<KoObjectDeclaration>` — objects implementing one of the four message markers |
| `pulseScreens()` | `List<KoFileDeclaration>` — files with a composable outside a `section` and a `component` package |
| `pulseSections()` | `List<KoFileDeclaration>` — files with a composable under a `section` package, components inside it excluded |
| `pulseComponents()` | `List<KoFileDeclaration>` — files with a composable under a `component` package |
| `pulseRepositoryClasses()` | `List<KoClassDeclaration>` — classes whose name ends with `Repository` |
| `pulseRepositoryInterfaces()` | `List<KoInterfaceDeclaration>` — interfaces whose name ends with `Repository` |
| `pulseRepositories()` | `List<KoBaseDeclaration>` — both of the above |
| `pulseKoinModuleFiles()` | `List<KoFileDeclaration>` — files that declare a Koin module |

## Scopes

The assertions work on any `KoScope`, so the choice of scope decides what is checked.

| Scope | What it reads |
|---|---|
| `Konsist.scopeFromProduction()` | every main source set of the project, test code left out |
| `Konsist.scopeFromProject()` | every source set, tests included |
| `Konsist.scopeFromProject(moduleName = "app")` | one module |
| `Konsist.scopeFromPackage("com.example.counter..")` | one package and everything under it |

## Next steps

- [Architecture checks guide](/guide/architecture-checks) — setup and the reasoning behind each check
- [Marker Interfaces](/api/interfaces) — the types the checks are about
