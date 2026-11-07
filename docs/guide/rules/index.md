# Rules

The rules are what PulsePlatform asks of the code around it, written as [Konsist](https://docs.konsist.lemonappdev.com/) assertions that your own test suite runs. They are published as the `architecture` artifact and run on the JVM, so they check desktop apps and the Kotlin of Android and iOS apps alike. They recognise the Pulse types by their fully qualified names and depend on neither `mvi-desktop` nor `mvi-mobile`, so adding them to a mobile app's tests puts no desktop classes on the classpath.

The compiler already rejects what the types can express: a ViewModel must implement `onAction`, and its message types must be the ones its Container knows. The rules cover what the types cannot, each a habit with a consequence that is easy to miss in review and easy for an AI agent to drift from. A failure names the file or declaration and says how to fix it, so a person or an agent can act on it without opening this site.

The rules come in groups, one assertion each. Every group can be used without the others.

| Assertion | What it checks | Page |
|---|---|---|
| `assertPulseMviConventions()` | a desktop feature split into a host, contents, sections and components; states and messages; previews | [UI structure](/guide/rules/ui) |
| `assertPulseMobileConventions()` | a mobile feature as one screen and one ViewModel; states and messages; previews | [UI structure](/guide/rules/ui#mobile) |
| `assertPulseDomainConventions()` | how a repository hands out its data, and what earns a use case its place | [Domain and data](/guide/rules/domain) |
| `assertPulseModuleConventions()` | which module may depend on which, and the packages inside the domain, the data layer and the design system | [Modules and Koin](/guide/rules/modules) |
| `assertPulseKoinConventions()` | where Koin modules are declared | [Modules and Koin](/guide/rules/modules#koin) |
| `assertPulseResourceConventions()` | where the text a user reads comes from | [Text](/guide/rules/text) |

## Setup

Add the artifact to the test classpath, then write one test that runs the groups the project uses. Konsist itself comes with the artifact, so there is nothing else to declare. `scopeFromProduction()` reads the project's main source sets, `commonMain` and `iosMain` included, and leaves test code out.

```kotlin
// build.gradle.kts
dependencies {
    testImplementation("com.github.kaleidot725.PulsePlatform:architecture:<version>")
}
```

```kotlin
class PulsePlatformConventionTest {
    @Test
    fun `follows the PulsePlatform conventions`() {
        val scope = Konsist.scopeFromProduction()
        scope.assertPulseMviConventions() // assertPulseMobileConventions() on mobile
        scope.assertPulseDomainConventions()
        scope.assertPulseModuleConventions()
        scope.assertPulseKoinConventions()
        scope.assertPulseResourceConventions()
    }
}
```

## Choosing the rules

Each check is public on its own. A project that disagrees with one can run the others.

```kotlin
@Test
fun `follows the conventions we agreed on`() {
    val scope = Konsist.scopeFromProduction()
    scope.assertStatesAreDataTypes()
    scope.assertMessagesAreSealedOrDataTypes()
    scope.assertViewModelsHoldTheirDataInTheirState()
}
```

## Writing your own rules

The queries behind the checks are public too, so a project can add rules of its own on top of them. `pulseViewModels()`, `pulseContainers()`, `pulseStateClasses()`, `pulseStateObjects()`, `pulseMessageInterfaces()`, `pulseMessageClasses()` and `pulseMessageObjects()` return the declarations that extend or implement the matching type. `pulseFeatureRoots()` returns the root package of each feature, and `pulseHosts()`, `pulseContents()`, `pulseSections()` and `pulseComponents()` return the files that hold composables of each role. On mobile, `pulseMobileFeatureRoots()` and `pulseScreens()` do the same for the screens. `pulseRepositoryClasses()`, `pulseRepositoryInterfaces()` and `pulseRepositories()` return the repositories, `pulseKoinModuleFiles()` the files that declare Koin modules, and `pulseModule(layout)` names the module a file belongs to.

```kotlin
@Test
fun `every ViewModel lives in a feature package`() {
    Konsist
        .scopeFromProduction()
        .pulseViewModels()
        .assertTrue { it.resideInPackage("..feature..") }
}
```

## What the rules cannot see

Konsist has no plugin mechanism, unlike ktlint's rule sets or detekt's plugins. The artifact cannot install itself, which is why your project writes the test that calls it.

Konsist also reads source code rather than compiled classes. A ViewModel that reaches `PulseViewModel` through a base class from a library is not recognised, because that base class is not in the sources being scanned.

Konsist does not read Gradle build scripts either, so the module checks find dependencies through imports. A dependency that is declared in a build script but never imported is not seen.

An empty result passes. A module with no PulsePlatform types yet does not fail, so the test can be added before the code it checks.

## Next steps

- [UI structure](/guide/rules/ui) — the rules at the centre of PulsePlatform
- [Building with AI agents](/guide/ai-agents) — let an agent run the rules on its own work
- [Rules API](/api/rules) — the signature of every assertion and query
