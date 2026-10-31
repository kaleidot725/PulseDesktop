package jp.kaleidot725.pulse.architecture

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.verify.assertTrue

/**
 * Checks the module layout PulseDesktop expects of a multi-module app: modules named after Now in Android's, with the
 * dependencies between them pointing the way Clean Architecture asks.
 *
 * | Module | Layer |
 * |---|---|
 * | `app` | the application, which wires everything together |
 * | `feature/<name>` | one feature's screens, sections and ViewModels |
 * | `core/domain` | the entities (`model`), the repository interfaces (`repository`) and any use cases (`usecase`) |
 * | `core/data` | the repository implementations (`repository`), their data sources (`local`) and what those store (`model`) |
 * | `core/designsystem` | the theme and the building blocks every screen is drawn with, one package per kind (`button`, `theme`), knowing nothing of the domain |
 * | `core/ui` | composables shared between features that draw the domain's entities |
 * | any other `core/<name>` | support, such as `core/common` or `core/testing` |
 *
 * The domain is the centre and depends on nothing. Unlike Now in Android, where the domain module depends on the data
 * module, the domain here owns the repository interfaces and `core/data` implements them, so a feature reaches data only
 * through the domain and `app` hands it the implementation. Inside the domain and the data layer, the package says what a
 * declaration is, so a reader finds the repository interfaces in one place and the data sources in another.
 *
 * The names are a [PulseModuleLayout], which a project with names of its own replaces. Dependencies are read from
 * imports, so what is checked is the code that is actually used.
 *
 * ```kotlin
 * class PulseModuleConventionTest {
 *     @Test
 *     fun `follows the PulseDesktop module conventions`() {
 *         Konsist.scopeFromProduction().assertPulseModuleConventions()
 *     }
 * }
 * ```
 */
public fun KoScope.assertPulseModuleConventions(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    assertNothingDependsOnTheApp(layout)
    assertFeaturesDoNotDependOnEachOther(layout)
    assertCoreDoesNotDependOnFeatures(layout)
    assertDomainDependsOnNothing(layout)
    assertDataDependsOnlyOnTheDomain(layout)
    assertDesignSystemDependsOnNothing(layout)
    assertSharedUiDependsOnlyOnTheDomain(layout)
    assertFeaturesReachDataThroughTheDomain(layout)
    assertDomainIsFrameworkFree(layout)
    assertDomainIsSplitIntoItsPackages(layout)
    assertDataIsSplitIntoItsPackages(layout)
    assertDesignSystemIsSplitByKind(layout)
}

/** No module depends on the app: the application sits on top and wires everything else together. */
public fun KoScope.assertNothingDependsOnTheApp(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    assertModuleDependencies(
        layout = layout,
        testName = "nothing depends on the app module",
        additionalMessage = "Move what is needed out of the app into a core module.",
        checks = { it != ModuleLayer.App },
        allows = { it != ModuleLayer.App },
    )
}

/** A feature depends on no other feature, so each one can be built, tested and removed on its own. */
public fun KoScope.assertFeaturesDoNotDependOnEachOther(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    assertModuleDependencies(
        layout = layout,
        testName = "features do not depend on each other",
        additionalMessage = "Move what both features need into a core module, or navigate through the app.",
        checks = { it == ModuleLayer.Feature },
        allows = { it != ModuleLayer.Feature },
    )
}

/** No core module depends on a feature: features are built from the core, never the other way round. */
public fun KoScope.assertCoreDoesNotDependOnFeatures(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    assertModuleDependencies(
        layout = layout,
        testName = "core modules do not depend on features",
        additionalMessage = "Move the code the core module needs out of the feature.",
        checks = { it != ModuleLayer.App && it != ModuleLayer.Feature },
        allows = { it != ModuleLayer.Feature },
    )
}

/**
 * The domain depends on no other layer, only on support modules.
 *
 * It is the centre: the entities, the use cases and the repository interfaces live here, and the data layer implements
 * those interfaces — so a use case can be tested without a database.
 */
public fun KoScope.assertDomainDependsOnNothing(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    assertModuleDependencies(
        layout = layout,
        testName = "the domain depends on no other layer",
        additionalMessage = "Declare the interface in the domain and implement it in the data layer.",
        checks = { it == ModuleLayer.Domain },
        allows = { it == ModuleLayer.Support },
    )
}

/** The data layer depends only on the domain whose repository interfaces it implements. */
public fun KoScope.assertDataDependsOnlyOnTheDomain(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    assertModuleDependencies(
        layout = layout,
        testName = "the data layer depends only on the domain",
        additionalMessage = "Remove the dependency on the UI; a ViewModel turns the data into UI state.",
        checks = { it == ModuleLayer.Data },
        allows = { it == ModuleLayer.Domain || it == ModuleLayer.Support },
    )
}

/**
 * The design system depends on no other layer, only on support modules — not even on the domain.
 *
 * Its theme and building blocks take text, numbers and callbacks, so any screen can use them whatever it shows. A
 * building block that takes an entity belongs in `core/ui`, which is where the domain meets the shared UI.
 */
public fun KoScope.assertDesignSystemDependsOnNothing(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    assertModuleDependencies(
        layout = layout,
        testName = "the design system depends on no other layer",
        additionalMessage = "Take plain values instead of the entity, or move the composable to core/ui.",
        checks = { it == ModuleLayer.DesignSystem },
        allows = { it == ModuleLayer.Support },
    )
}

/**
 * The shared UI depends only on the domain, the design system and itself: it draws the entities it is given and
 * fetches nothing.
 */
public fun KoScope.assertSharedUiDependsOnlyOnTheDomain(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    assertModuleDependencies(
        layout = layout,
        testName = "the shared UI depends only on the domain",
        additionalMessage = "Take the data as a parameter; fetching it belongs to a feature's ViewModel.",
        checks = { it == ModuleLayer.SharedUi },
        allows = {
            it == ModuleLayer.Domain || it == ModuleLayer.DesignSystem || it == ModuleLayer.SharedUi || it == ModuleLayer.Support
        },
    )
}

/** A feature depends on the domain, never on the data layer: it reaches data through a repository interface or a use case. */
public fun KoScope.assertFeaturesReachDataThroughTheDomain(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    assertModuleDependencies(
        layout = layout,
        testName = "features reach data through the domain",
        additionalMessage = "Depend on a use case or repository interface in the domain; the app provides the implementation.",
        checks = { it == ModuleLayer.Feature },
        allows = { it != ModuleLayer.Data },
    )
}

/**
 * The domain imports nothing from Compose, AndroidX Lifecycle or PulseDesktop.
 *
 * The centre is plain Kotlin. A use case that needs Compose can only run where Compose runs, and the reason to have a
 * domain is that it does not care.
 */
public fun KoScope.assertDomainIsFrameworkFree(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    files
        .filter { it.layerIn(layout) == ModuleLayer.Domain }
        .assertTrue(
            testName = "the domain is framework-free",
            additionalMessage = "Move the framework code to a feature or to the shared UI.",
        ) { file -> file.imports.none { import -> frameworkPackages.any { import.name.startsWith(it) } } }
}

/**
 * Every file in the domain sits in a `model`, `repository` or `usecase` package.
 *
 * The entities, the repository interfaces and the use cases are read for different reasons, so each kind has a package
 * of its own. `usecase` is there only for a project that has use cases; a domain without them uses the other two.
 */
public fun KoScope.assertDomainIsSplitIntoItsPackages(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    assertLayerPackages(
        layout = layout,
        layer = ModuleLayer.Domain,
        packages = domainPackages,
        testName = "the domain is split into model, repository and usecase packages",
    )
}

/**
 * Every file in the data layer sits in a `repository`, `local` or `model` package.
 *
 * `repository` holds the implementations of the domain's interfaces, `local` the data sources they read and write, and
 * `model` the shapes those sources store, which stay out of the domain. A data source only persists — it reads, writes
 * and removes — and the repository decides how a value changes, such as adding one to a count.
 */
public fun KoScope.assertDataIsSplitIntoItsPackages(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    assertLayerPackages(
        layout = layout,
        layer = ModuleLayer.Data,
        packages = dataPackages,
        testName = "the data layer is split into repository, local and model packages",
    )
}

/**
 * Every file in the design system sits in a package named after the kind of building block it holds — `button`, `card`,
 * `theme` — not at the module's root or under a catch-all such as `component`.
 *
 * Everything in a design system is a building block, so a package called `component` or `widget` says nothing about
 * what is inside it, and it fills up until finding a button means reading the whole list. A package per kind tells a
 * reader where a button is, and shows when two of them do the same job.
 *
 * The root is the package named after the module's directory, `designsystem` for `core/designsystem`. These are not
 * the components of the MVI conventions, which are the stateless parts of one screen; those still live in a `component`
 * package inside a feature.
 */
public fun KoScope.assertDesignSystemIsSplitByKind(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    files
        .filter { it.layerIn(layout) == ModuleLayer.DesignSystem }
        .assertTrue(
            testName = "the design system is split into a package per kind",
            additionalMessage = "Move the file into a package named after what it is, such as `button`, `card` or `theme`.",
        ) { file ->
            val kinds = file.packagesBelowModuleRoot()
            kinds.isNotEmpty() && kinds.none { it in catchAllPackages }
        }
}

/**
 * The module a file belongs to under [layout], as the part of its directory a pattern matched — `core/data` or
 * `feature/counter` — or null when no layer claims it.
 */
public fun KoFileDeclaration.pulseModule(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid): String? =
    layout.moduleOf(moduleDirectory)

internal enum class ModuleLayer { App, Feature, Domain, Data, DesignSystem, SharedUi, Support }

private val domainPackages = listOf("model", "repository", "usecase")

private val dataPackages = listOf("repository", "local", "model")

/** Package names that say a declaration is a building block, which everything in a design system is, and not which. */
private val catchAllPackages = listOf("component", "components", "widget", "widgets", "composable", "composables", "common")

/**
 * The packages below the one named after the module's directory, so `button` for `counter.core.designsystem.button` in
 * `core/designsystem`. When no package is named after the directory, every package counts.
 */
private fun KoFileDeclaration.packagesBelowModuleRoot(): List<String> {
    val packages = packagee?.name?.split('.').orEmpty()
    val root = moduleDirectory.substringAfterLast('/').filter { it.isLetterOrDigit() }.lowercase()
    return packages.drop(packages.indexOfLast { it.lowercase() == root } + 1)
}

private fun KoScope.assertLayerPackages(
    layout: PulseModuleLayout,
    layer: ModuleLayer,
    packages: List<String>,
    testName: String,
) {
    files
        .filter { it.layerIn(layout) == layer }
        .assertTrue(
            testName = testName,
            additionalMessage = "Move the file into a package named ${packages.joinToString { "`$it`" }}.",
        ) { file ->
            file.packagee
                ?.name
                ?.split('.')
                .orEmpty()
                .any { it in packages }
        }
}

private fun KoScope.assertModuleDependencies(
    layout: PulseModuleLayout,
    testName: String,
    additionalMessage: String,
    checks: (ModuleLayer) -> Boolean,
    allows: (ModuleLayer) -> Boolean,
) {
    val packages = packageIndex(layout)
    files
        .filter { file -> file.layerIn(layout)?.let(checks) == true }
        .assertTrue(testName = testName, additionalMessage = additionalMessage) { file ->
            file.moduleDependencies(packages, own = file.pulseModule(layout)).all { (_, layer) -> allows(layer) }
        }
}

/** Every package in the scope, mapped to the module that declares it and that module's layer. */
private fun KoScope.packageIndex(layout: PulseModuleLayout): Map<String, Pair<String, ModuleLayer>> =
    files
        .mapNotNull { file ->
            val module = file.pulseModule(layout) ?: return@mapNotNull null
            val layer = file.layerIn(layout) ?: return@mapNotNull null
            file.packagee?.name?.let { it to (module to layer) }
        }.toMap()

/** The other modules this file imports from, found by the longest package that prefixes each import. */
private fun KoFileDeclaration.moduleDependencies(
    packages: Map<String, Pair<String, ModuleLayer>>,
    own: String?,
): Set<Pair<String, ModuleLayer>> =
    imports
        .mapNotNull { import ->
            generateSequence(import.name) { it.substringBeforeLast('.', "").ifEmpty { null } }
                .firstNotNullOfOrNull(packages::get)
        }.filter { (module, _) -> module != own }
        .toSet()

/** The layer is read from the directory, so it holds even for a feature named after a trimmed `api` or `impl`. */
internal fun KoFileDeclaration.layerIn(layout: PulseModuleLayout): ModuleLayer? = layout.layerOf(moduleDirectory)

/** The directory above the last `src`, where Gradle keeps a module's sources. */
private val KoFileDeclaration.moduleDirectory: String
    get() = path.replace('\\', '/').substringBeforeLast("/src/")

private val frameworkPackages =
    listOf(
        "androidx.compose.",
        "androidx.lifecycle.",
        "jp.kaleidot725.pulse.mvi.",
        "jp.kaleidot725.pulse.navigation.",
        "jp.kaleidot725.pulse.architecture.",
    )
