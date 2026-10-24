package jp.kaleidot725.pulse.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.core.exception.KoAssertionFailedException
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * The module assertions this artifact publishes, run over an app shaped like Now in Android and over the same app with
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
                "feature/foryou",
                "core/model",
                "core/common",
                "core/domain",
                "core/data",
                "core/database",
                "core/designsystem",
                "core/ui",
            ),
            conforming.files.mapNotNull { it.pulseModule }.toSet(),
        )

        conforming.assertPulseModuleConventions()
    }

    /**
     * The demo is a single module outside the `app` / `feature` / `core` layout, which the checks must leave alone.
     */
    @Test
    fun `passes on the demo application`() {
        Konsist.scopeFromProject(moduleName = "demo").assertPulseModuleConventions()
    }

    /**
     * `feature/foryou/api` and `feature/foryou/impl` are one feature, so the implementation may use its own API without
     * counting as one feature depending on another.
     */
    @Test
    fun `reads an api and impl split as one feature`() {
        val modules = conforming.files.filter { it.name in setOf("ForYouRoute", "ForYouScreen") }.map { it.pulseModule }

        assertEquals(listOf("feature/foryou", "feature/foryou"), modules)
        conforming.assertFeaturesDoNotDependOnEachOther()
    }

    /** A `core` module that reaches into `app` can no longer be built without the whole application. */
    @Test
    fun `reports a module that depends on the app`() {
        assertReports("CommonKnowsTheApp") { assertNothingDependsOnTheApp() }
    }

    /** A feature that reaches into another cannot be built, tested or removed on its own. */
    @Test
    fun `reports a feature that depends on another feature`() {
        assertReports("CounterOpensSettings") { assertFeaturesDoNotDependOnEachOther() }
    }

    /** A `core` module that knows a feature turns the direction of the whole graph around. */
    @Test
    fun `reports a core module that depends on a feature`() {
        assertReports("CommonKnowsAFeature") { assertCoreDoesNotDependOnFeatures() }
    }

    /** The model is the innermost circle; one import outward and every layer depends on everything. */
    @Test
    fun `reports a model that depends on another module`() {
        assertReports("ModelKnowsTheDomain") { assertModelDependsOnNothing() }
    }

    /** A domain that imports the data module cannot test a use case without a database. */
    @Test
    fun `reports a domain that depends on the data module`() {
        assertReports("DomainKnowsTheData") { assertDomainDependsOnlyOnModel() }
    }

    /** A data module that draws UI ties the data to one way of showing it. */
    @Test
    fun `reports a data module that depends on the UI`() {
        assertReports("DataKnowsTheUi") { assertDataDependsOnlyOnDomainAndDataSources() }
    }

    /** A data source that knows the domain is no longer just storage. */
    @Test
    fun `reports a data source that depends on the domain`() {
        assertReports("DatabaseKnowsTheDomain") { assertDataSourcesDependOnlyOnModel() }
    }

    /** Shared UI that runs a use case fetches its own data, which every feature using it then inherits. */
    @Test
    fun `reports shared UI that depends on the domain`() {
        assertReports("UiKnowsTheDomain") { assertSharedUiDependsOnlyOnModel() }
    }

    /** A feature that reads the database skips the use cases, and the domain rules with them. */
    @Test
    fun `reports a feature that reaches a data source`() {
        assertReports("SettingsReadsTheDatabase") { assertFeaturesReachDataThroughTheDomain() }
    }

    /** A model annotated for Compose can only be used where Compose runs. */
    @Test
    fun `reports a model that imports Compose`() {
        assertReports("ComposeModel") { assertModelAndDomainAreFrameworkFree() }
    }

    private fun assertReports(
        name: String,
        assertion: KoScope.() -> Unit,
    ) {
        val failure = assertFailsWith<KoAssertionFailedException> { violating.assertion() }
        val message = failure.message ?: ""
        check(message.contains(name)) { "Expected the failure to name $name, but it said: $message" }
    }

    private fun fixture(path: String) = "architecture/src/jvmTest/resources/fixture/modules/$path"

    private val conforming get() = Konsist.scopeFromDirectory(fixture("conforming"))

    private val violating get() = conforming + Konsist.scopeFromDirectory(fixture("violating"))
}
