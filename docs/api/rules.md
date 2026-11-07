# Rules

Everything on this page lives in the `architecture` artifact. The package is `jp.kaleidot725.pulse.architecture`, and every declaration is an extension of Konsist's `KoScope`.

For what the checks mean and why they exist, see the [Rules guide](/guide/rules/).

## assertPulseMviConventions

```kotlin
fun KoScope.assertPulseMviConventions()
```

Runs the twelve checks that follow, from `assertStatesAreDataTypes` to `assertSectionsAndComponentsHavePreviews`, in order. A composable counts as a view only when it returns nothing; one that returns a value, such as a label read with `stringResource`, is skipped by every view check. The first failing check throws, so a run reports one problem at a time.

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

## assertStatesAndMessagesLiveInContracts

```kotlin
fun KoScope.assertStatesAndMessagesLiveInContracts()
```

Requires every class, object or interface that implements `PulseState`, `PulseAction`, `PulseEvent`, `PulseBroadcast` or `PulseUnicast` directly to be in a file whose name ends with `Contracts`, in the package of a `PulseViewModel` or `PulseContainer` subclass. Cases nested inside a sealed message are not checked on their own.

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

## assertViewModelsLiveInContents

```kotlin
fun KoScope.assertViewModelsLiveInContents()
```

Requires every `PulseViewModel` subclass to be in a package with a `content` segment and with neither a `section` nor a `component` segment.

## assertContainersLiveAtTheFeatureRoot

```kotlin
fun KoScope.assertContainersLiveAtTheFeatureRoot()
```

Requires every `PulseContainer` subclass to be in a feature root, a package that a `content` package sits in.

## assertHostsComposeContents

```kotlin
fun KoScope.assertHostsComposeContents()
```

Requires every file with a composable in a feature root — a host — to have a name ending in `Host`, and not to call `PulseContent`.

## assertFeaturePackagesHaveARole

```kotlin
fun KoScope.assertFeaturePackagesHaveARole()
```

Requires every file inside a feature, below its root, to be under a `content`, `section` or `component` package, whether it holds a composable or not. Files under a `build/generated` directory are skipped.

## assertViewsHaveARole

```kotlin
@Deprecated
fun KoScope.assertViewsHaveARole()
```

Deprecated in favour of `assertFeaturePackagesHaveARole`, and no longer run by `assertPulseMviConventions`. Requires every file with a composable inside a feature, below its root, to be under a `content`, `section` or `component` package.

## assertContentsBindAViewModel

```kotlin
fun KoScope.assertContentsBindAViewModel()
```

Requires every file with a composable under a `content` package, outside its `section` and `component` packages, to call `PulseContent`.

## assertSectionsAndComponentsArePure

```kotlin
fun KoScope.assertSectionsAndComponentsArePure()
```

Requires every composable under a `section` or `component` package to take no parameter whose type name ends in `ViewModel` or `Container`, and the file to call neither `PulseContent` nor `PulseHost`.

## assertSectionsAndComponentsHavePreviews

```kotlin
fun KoScope.assertSectionsAndComponentsHavePreviews()
```

Requires every public composable under a `section` or `component` package to have a function annotated `@Preview` — from any package, or a multipreview annotation whose name starts with `Preview` — named `<Name>_Preview` or `<Name>_Preview_<Condition>`. Previews are looked for across the whole scope. Private composables and the previews themselves are skipped.

## assertPulseMobileConventions

```kotlin
fun KoScope.assertPulseMobileConventions()
```

Runs the checks for an app built on `mvi-mobile`, in order: `assertStatesAreDataTypes`, `assertMessagesAreSealedOrDataTypes`, `assertStatesAndMessagesLiveInContracts`, `assertViewModelsAndContainersAreNamedAfterTheirBase`, `assertViewModelsHoldTheirDataInTheirState`, the three mobile checks that follow, `assertSectionsAndComponentsArePure` and `assertSectionsAndComponentsHavePreviews`. The first failing check throws.

## assertMobileFeaturesHaveOneViewModel

```kotlin
fun KoScope.assertMobileFeaturesHaveOneViewModel()
```

Requires every `PulseViewModel` subclass to be outside any `section`, `component` or `content` package, and to be the only one in its package.

## assertScreensBindTheirViewModel

```kotlin
fun KoScope.assertScreensBindTheirViewModel()
```

Requires every file with a composable at the root of a mobile feature to be named `…Screen` and to call `PulseContent`, and every `PulseViewModel` subclass to have such a file in its package.

## assertMobileFeaturePackagesHaveARole

```kotlin
fun KoScope.assertMobileFeaturePackagesHaveARole()
```

Requires every file below the root of a mobile feature to be under a `section` or `component` package. Files under `build/generated` are skipped.

## assertPulseDomainConventions

```kotlin
fun KoScope.assertPulseDomainConventions()
```

Runs the two repository checks and the two use case checks below. A repository is a class or an interface whose name ends with `Repository`, and classes and interfaces are checked in one pass, so a failure names every offender at once. A use case is a class whose name ends with `UseCase`.

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

## assertUseCasesLiveInTheUseCasePackage

```kotlin
fun KoScope.assertUseCasesLiveInTheUseCasePackage()
```

Requires every use case to be in a package with a `usecase` segment.

## assertUseCasesDoMoreThanOneLine

```kotlin
fun KoScope.assertUseCasesDoMoreThanOneLine()
```

Requires every public function of a use case to have a body of more than one non-blank line. The body is what follows `=` for an expression body, or what sits between the braces for a block body. Private functions are not checked.

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
| `assertDesignSystemDependsOnNothing(layout)` | the design system imports only from support modules |
| `assertSharedUiDependsOnlyOnTheDomain(layout)` | the shared UI imports only from the domain, the design system, itself and support modules |
| `assertFeaturesReachDataThroughTheDomain(layout)` | a feature imports nothing from the data layer |
| `assertDomainIsFrameworkFree(layout)` | the domain imports nothing from `androidx.compose`, `androidx.lifecycle`, `jp.kaleidot725.pulse.mvi`, `jp.kaleidot725.pulse.navigation` or `jp.kaleidot725.pulse.architecture` |
| `assertDomainIsSplitIntoItsPackages(layout)` | every domain file is in a package with a `model`, `repository` or `usecase` segment |
| `assertDataIsSplitIntoItsPackages(layout)` | every data file is in a package with a `repository`, `local` or `model` segment |
| `assertDesignSystemIsSplitByKind(layout)` | every design system file is in a package below the one named after the module's directory, and none of its packages is a catch-all such as `component`, `widget`, `composable` or `common` |

## PulseModuleLayout

```kotlin
data class PulseModuleLayout(
    val app: List<String> = listOf("app"),
    val features: List<String> = listOf("feature/*", "feature/*/api", "feature/*/impl"),
    val domain: List<String> = listOf("core/domain"),
    val data: List<String> = listOf("core/data"),
    val sharedUi: List<String> = listOf("core/ui"),
    val support: List<String> = listOf("core/*"),
    val designSystem: List<String> = listOf("core/designsystem"),
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

## assertPulseResourceConventions

```kotlin
fun KoScope.assertPulseResourceConventions(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid)
```

Runs the two resource checks below.

## assertDomainHoldsNoText

```kotlin
fun KoScope.assertDomainHoldsNoText(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid)
```

Requires no string literal in a domain file to contain a letter once its `$value` and `${expression}` templates are taken out. Comments are skipped. Files in a module no layer claims are not checked.

## assertComposablesTakeTextFromResources

```kotlin
fun KoScope.assertComposablesTakeTextFromResources()
```

Requires every composable that is not a `@Preview` to hand no string literal with a letter in it to `Text(…)` or to a `text`, `title`, `label`, `placeholder` or `contentDescription` argument. Templates are taken out first, so a literal that only places values passes.

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
| `pulseFeatureRoots()` | `Set<String>` — the packages a `content` package sits in, one per feature |
| `pulseHosts()` | `List<KoFileDeclaration>` — files with a composable in a feature root |
| `pulseContents()` | `List<KoFileDeclaration>` — files with a composable under a `content` package, outside its `section` and `component` packages |
| `pulseSections()` | `List<KoFileDeclaration>` — files with a composable under a `section` package, components inside it excluded |
| `pulseComponents()` | `List<KoFileDeclaration>` — files with a composable under a `component` package |
| `pulseMobileFeatureRoots()` | `Set<String>` — the packages a `PulseViewModel` sits in, outside `section` and `component`, one per mobile feature |
| `pulseScreens()` | `List<KoFileDeclaration>` — files with a composable at the root of a mobile feature |
| `pulseRepositoryClasses()` | `List<KoClassDeclaration>` — classes whose name ends with `Repository` |
| `pulseRepositoryInterfaces()` | `List<KoInterfaceDeclaration>` — interfaces whose name ends with `Repository` |
| `pulseRepositories()` | `List<KoBaseDeclaration>` — both of the above |
| `pulseUseCases()` | `List<KoClassDeclaration>` — classes whose name ends with `UseCase` |
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

- [Rules guide](/guide/rules/) — setup and the reasoning behind each check
- [Marker Interfaces](/api/interfaces) — the types the checks are about
