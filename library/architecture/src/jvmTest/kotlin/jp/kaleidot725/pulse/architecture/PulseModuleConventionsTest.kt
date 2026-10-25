package jp.kaleidot725.pulse.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.core.exception.KoAssertionFailedException
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * The module assertions this artifact publishes, run over a counter app split into modules and over the same app with
 * one dependency added that breaks each rule.
 */
class PulseModuleConventionsTest {
    /**
     * Every module the layout names is present, so the pass cannot come from a path the layout failed to recognise.
     */
    @Test
    fun `passes on an app that follows the conventions`() {
        assertEquals(
            setOf(
                "app",
                "feature/counter",
                "feature/settings",
                "feature/history",
                "core/common",
                "core/domain",
                "core/data",
                "core/designsystem",
                "core/ui",
            ),
            conforming.files.mapNotNull { it.pulseModule() }.toSet(),
        )

        conforming.assertPulseModuleConventions()
    }

    /**
     * The demo is split the way the default layout expects — an app, a feature, and a domain, data and design system
     * under `core` — so the rules are known to hold on a real build, not only on fixtures. The layout matches the end of
     * each directory, which is why the `demo` above them does not get in the way.
     */
    @Test
    fun `passes on the demo application`() {
        val demo = Konsist.scopeFromDirectory("demo")

        assertEquals(
            setOf("app", "feature/count", "core/domain", "core/data", "core/designsystem"),
            demo.files.mapNotNull { it.pulseModule() }.toSet(),
        )
        demo.assertPulseModuleConventions()
    }

    /**
     * `feature/history/api` and `feature/history/impl` are one feature, so the implementation may use its own API without
     * counting as one feature depending on another.
     */
    @Test
    fun `reads an api and impl split as one feature`() {
        val modules = conforming.files.filter { it.name in setOf("HistoryRoute", "HistoryScreen") }.map { it.pulseModule() }

        assertEquals(listOf("feature/history", "feature/history"), modules)
        conforming.assertFeaturesDoNotDependOnEachOther()
    }

    /**
     * A project with names of its own is not recognised under the default names, and so is not checked at all — which
     * is why the names can be replaced.
     */
    @Test
    fun `leaves a project with other module names alone under the default layout`() {
        assertEquals(emptySet(), custom.files.mapNotNull { it.pulseModule() }.toSet())
    }

    /** The same rules hold under a layout that maps each layer to the project's own names. */
    @Test
    fun `checks a project with its own module names under a custom layout`() {
        assertEquals(
            setOf("desktop", "features/catalog", "shared/domain", "shared/data"),
            custom.files.mapNotNull { it.pulseModule(shopLayout) }.toSet(),
        )

        custom.assertPulseModuleConventions(shopLayout)
    }

    /** A violation is found under the custom names exactly as it is under the default ones. */
    @Test
    fun `reports a violation under a custom layout`() {
        val violating = custom + Konsist.scopeFromDirectory(fixture("custom-violating"))

        assertReports("DomainKnowsTheTable", violating) { assertDomainDependsOnNothing(shopLayout) }
    }

    /** A core module that reaches into the app can no longer be built without the whole application. */
    @Test
    fun `reports a module that depends on the app`() {
        assertReports("CommonKnowsTheApp") { assertNothingDependsOnTheApp() }
    }

    /** A feature that reaches into another cannot be built, tested or removed on its own. */
    @Test
    fun `reports a feature that depends on another feature`() {
        assertReports("CounterOpensSettings") { assertFeaturesDoNotDependOnEachOther() }
    }

    /** A core module that knows a feature turns the direction of the whole graph around. */
    @Test
    fun `reports a core module that depends on a feature`() {
        assertReports("CommonKnowsAFeature") { assertCoreDoesNotDependOnFeatures() }
    }

    /** A domain that imports the data layer cannot test a use case without the storage behind it. */
    @Test
    fun `reports a domain that depends on the data layer`() {
        assertReports("DomainKnowsTheData") { assertDomainDependsOnNothing() }
    }

    /** A data layer that draws UI ties the data to one way of showing it. */
    @Test
    fun `reports a data layer that depends on the UI`() {
        assertReports("DataKnowsTheUi") { assertDataDependsOnlyOnTheDomain() }
    }

    /** Shared UI that holds a repository fetches its own data, which every feature using it then inherits. */
    @Test
    fun `reports shared UI that depends on the data layer`() {
        assertReports("UiKnowsTheData") { assertSharedUiDependsOnlyOnTheDomain() }
    }

    /**
     * A design system that takes an entity can only draw that entity, so the building blocks stop being shared by
     * every screen; such a composable belongs in `core/ui`.
     */
    @Test
    fun `reports a design system that depends on the domain`() {
        assertReports("DesignSystemKnowsTheDomain") { assertDesignSystemDependsOnNothing() }
    }

    /** The shared UI may use the design system, which is how `CounterCard` draws a `PulseButton`. */
    @Test
    fun `lets the shared UI use the design system`() {
        conforming.assertSharedUiDependsOnlyOnTheDomain()
    }

    /** An entity left at the root of the domain is mixed in with the repository interfaces and the use cases. */
    @Test
    fun `reports a domain file outside the model, repository and usecase packages`() {
        assertReports("UnsortedEntity") { assertDomainIsSplitIntoItsPackages() }
    }

    /** A class left at the root of the data layer cannot be told apart from a repository or a data source. */
    @Test
    fun `reports a data file outside the repository, local and model packages`() {
        assertReports("UnsortedCache") { assertDataIsSplitIntoItsPackages() }
    }

    /** A feature that holds a repository implementation skips the use cases, and the domain rules with them. */
    @Test
    fun `reports a feature that reaches the data layer`() {
        assertReports("SettingsReadsTheData") { assertFeaturesReachDataThroughTheDomain() }
    }

    /** An entity annotated for Compose can only be used where Compose runs. */
    @Test
    fun `reports a domain that imports Compose`() {
        assertReports("ComposeCounter") { assertDomainIsFrameworkFree() }
    }

    private fun assertReports(
        name: String,
        scope: KoScope = violating,
        assertion: KoScope.() -> Unit,
    ) {
        val failure = assertFailsWith<KoAssertionFailedException> { scope.assertion() }
        val message = failure.message ?: ""
        check(message.contains(name)) { "Expected the failure to name $name, but it said: $message" }
    }

    private fun fixture(path: String) = "library/architecture/src/jvmTest/resources/fixture/modules/$path"

    private val conforming get() = Konsist.scopeFromDirectory(fixture("conforming"))

    private val violating get() = conforming + Konsist.scopeFromDirectory(fixture("violating"))

    private val custom get() = Konsist.scopeFromDirectory(fixture("custom"))

    private val shopLayout =
        PulseModuleLayout(
            app = listOf("desktop"),
            features = listOf("features/*"),
            domain = listOf("shared/domain"),
            data = listOf("shared/data"),
            sharedUi = listOf("shared/widget"),
            support = listOf("shared/*"),
        )
}
