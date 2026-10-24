package jp.kaleidot725.pulse.architecture

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.verify.assertTrue

/**
 * Checks the module layout PulseDesktop expects of a multi-module app: the modules of Now in Android, with the
 * dependencies between them pointing the way Clean Architecture asks.
 *
 * | Module | Layer |
 * |---|---|
 * | `app` | the application, which wires everything together |
 * | `feature/<name>` | one feature's screens, sections and ViewModels |
 * | `core/model` | the entities every layer shares |
 * | `core/domain` | use cases, and the repository interfaces they need |
 * | `core/data` | the repository implementations |
 * | `core/database`, `core/network`, `core/datastore` | the data sources the implementations read |
 * | `core/designsystem`, `core/ui` | composables shared between features |
 * | any other `core/<name>` | support, such as `core/common` or `core/testing` |
 *
 * Unlike Now in Android, where the domain module depends on the data module, the domain here owns the repository
 * interfaces and the data module implements them: the domain depends on nothing but the model, and a feature reaches
 * data only through a use case.
 *
 * Dependencies are read from imports, so what is checked is the code that is actually used. A module is recognised by
 * its path, `app/src/…`, `feature/<name>/src/…` or `core/<name>/src/…`; a `feature/<name>/api` or `/impl` split counts
 * as one feature. Files outside that layout are not checked.
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
    assertModelDependsOnNothing(layout)
    assertDomainDependsOnlyOnModel(layout)
    assertDataDependsOnlyOnDomainAndDataSources(layout)
    assertDataSourcesDependOnlyOnModel(layout)
    assertSharedUiDependsOnlyOnModel(layout)
    assertFeaturesReachDataThroughTheDomain(layout)
    assertModelAndDomainAreFrameworkFree(layout)
}

/** No module depends on `app`: the application sits on top and wires everything else together. */
public fun KoScope.assertNothingDependsOnTheApp(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    assertModuleDependencies(
        layout = layout,
        testName = "nothing depends on the app module",
        additionalMessage = "Move what is needed out of `app` into a `core` module.",
        checks = { it != ModuleLayer.App },
        allows = { _, to -> to != ModuleLayer.App },
    )
}

/** A feature depends on no other feature, so each one can be built, tested and removed on its own. */
public fun KoScope.assertFeaturesDoNotDependOnEachOther(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    assertModuleDependencies(
        layout = layout,
        testName = "features do not depend on each other",
        additionalMessage = "Move what both features need into a `core` module, or navigate through `app`.",
        checks = { it == ModuleLayer.Feature },
        allows = { _, to -> to != ModuleLayer.Feature },
    )
}

/** No `core` module depends on a feature: features are built from `core`, never the other way round. */
public fun KoScope.assertCoreDoesNotDependOnFeatures(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    assertModuleDependencies(
        layout = layout,
        testName = "core modules do not depend on features",
        additionalMessage = "Move the code the core module needs out of the feature.",
        checks = { it.isCore },
        allows = { _, to -> to != ModuleLayer.Feature },
    )
}

/** `core/model` depends on no other module: the entities are the innermost circle. */
public fun KoScope.assertModelDependsOnNothing(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    assertModuleDependencies(
        layout = layout,
        testName = "the model module depends on no other module",
        additionalMessage = "Keep `core/model` to plain data; move the behaviour to `core/domain`.",
        checks = { it == ModuleLayer.Model },
        allows = { _, _ -> false },
    )
}

/**
 * `core/domain` depends only on `core/model` and support modules.
 *
 * The domain owns the repository interfaces and `core/data` implements them, so the domain never needs the data
 * module — which is what keeps the use cases testable without a database.
 */
public fun KoScope.assertDomainDependsOnlyOnModel(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    assertModuleDependencies(
        layout = layout,
        testName = "the domain module depends only on the model",
        additionalMessage = "Declare the interface in `core/domain` and implement it in `core/data`.",
        checks = { it == ModuleLayer.Domain },
        allows = { _, to -> to == ModuleLayer.Model || to == ModuleLayer.Support },
    )
}

/** `core/data` depends only on the domain it implements, the model, and the data sources it reads. */
public fun KoScope.assertDataDependsOnlyOnDomainAndDataSources(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    assertModuleDependencies(
        layout = layout,
        testName = "the data module depends only on the domain, the model and the data sources",
        additionalMessage = "Remove the dependency on the UI; a ViewModel turns the data into UI state.",
        checks = { it == ModuleLayer.Data },
        allows = { _, to ->
            to in setOf(ModuleLayer.Domain, ModuleLayer.Model, ModuleLayer.DataSource, ModuleLayer.Support)
        },
    )
}

/** `core/database`, `core/network` and `core/datastore` depend only on the model: they are the outer edge. */
public fun KoScope.assertDataSourcesDependOnlyOnModel(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    assertModuleDependencies(
        layout = layout,
        testName = "the data source modules depend only on the model",
        additionalMessage = "Let `core/data` combine the data sources; a data source only stores or fetches.",
        checks = { it == ModuleLayer.DataSource },
        allows = { _, to -> to == ModuleLayer.Model || to == ModuleLayer.Support },
    )
}

/** `core/designsystem` and `core/ui` depend only on the model and each other: shared UI draws data, it fetches none. */
public fun KoScope.assertSharedUiDependsOnlyOnModel(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    assertModuleDependencies(
        layout = layout,
        testName = "the shared UI modules depend only on the model",
        additionalMessage = "Take the data as a parameter; fetching it belongs to a feature's ViewModel.",
        checks = { it == ModuleLayer.SharedUi },
        allows = { _, to ->
            to == ModuleLayer.Model || to == ModuleLayer.SharedUi || to == ModuleLayer.Support
        },
    )
}

/** A feature depends on `core/domain`, never on `core/data` or a data source: it reaches data through a use case. */
public fun KoScope.assertFeaturesReachDataThroughTheDomain(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    assertModuleDependencies(
        layout = layout,
        testName = "features reach data through the domain",
        additionalMessage = "Depend on a use case or repository interface in `core/domain`; `app` provides the implementation.",
        checks = { it == ModuleLayer.Feature },
        allows = { _, to -> to != ModuleLayer.Data && to != ModuleLayer.DataSource },
    )
}

/**
 * `core/model` and `core/domain` import nothing from Compose, AndroidX Lifecycle or PulseDesktop.
 *
 * The inner circles are plain Kotlin. A use case that needs Compose can only run where Compose runs, and the reason to
 * have a domain is that it does not care.
 */
public fun KoScope.assertModelAndDomainAreFrameworkFree(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    files
        .filter { it.layerIn(layout) == ModuleLayer.Model || it.layerIn(layout) == ModuleLayer.Domain }
        .assertTrue(
            testName = "the model and domain modules are framework-free",
            additionalMessage = "Move the framework code to a feature or to `core/ui`.",
        ) { file -> file.imports.none { import -> frameworkPackages.any { import.name.startsWith(it) } } }
}

/**
 * The module a file belongs to under [layout], as the part of its directory a pattern matched — `core/data` or
 * `feature/counter` — or null when no layer claims it.
 */
public fun KoFileDeclaration.pulseModule(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid): String? =
    layout.moduleOf(moduleDirectory)

internal enum class ModuleLayer { App, Feature, Model, Domain, Data, DataSource, SharedUi, Support }

private val ModuleLayer.isCore: Boolean
    get() = this != ModuleLayer.App && this != ModuleLayer.Feature

private fun KoScope.assertModuleDependencies(
    layout: PulseModuleLayout,
    testName: String,
    additionalMessage: String,
    checks: (ModuleLayer) -> Boolean,
    allows: (from: String, to: ModuleLayer) -> Boolean,
) {
    val packages = packageIndex(layout)
    files
        .filter { file -> file.layerIn(layout)?.let(checks) == true }
        .assertTrue(testName = testName, additionalMessage = additionalMessage) { file ->
            val from = file.pulseModule(layout)!!
            file.moduleDependencies(packages, from).all { (_, to) -> allows(from, to) }
        }
}

/** Every package in the scope mapped to the module that declares it. */
private fun KoScope.packageIndex(layout: PulseModuleLayout): Map<String, Pair<String, ModuleLayer>> =
    files
        .mapNotNull { file ->
            val module = file.pulseModule(layout) ?: return@mapNotNull null
            val layer = file.layerIn(layout) ?: return@mapNotNull null
            file.packagee?.name?.let { it to (module to layer) }
        }.toMap()

/** The layer is read from the directory, so it holds even for a module named after a trimmed `api` or `impl`. */
private fun KoFileDeclaration.layerIn(layout: PulseModuleLayout): ModuleLayer? = layout.layerOf(moduleDirectory)

/** The directory above the last `src`, where Gradle keeps a module's sources. */
private val KoFileDeclaration.moduleDirectory: String
    get() = path.replace('\\', '/').substringBeforeLast("/src/")

/** The other modules this file imports from, found by the longest package that prefixes each import. */
private fun KoFileDeclaration.moduleDependencies(
    packages: Map<String, Pair<String, ModuleLayer>>,
    own: String,
): Set<Pair<String, ModuleLayer>> =
    imports
        .mapNotNull { import ->
            generateSequence(import.name) { it.substringBeforeLast('.', "").ifEmpty { null } }
                .firstNotNullOfOrNull(packages::get)
        }.filter { (module, _) -> module != own }
        .toSet()

private val frameworkPackages = listOf("androidx.compose.", "androidx.lifecycle.", "jp.kaleidot725.pulse.")
