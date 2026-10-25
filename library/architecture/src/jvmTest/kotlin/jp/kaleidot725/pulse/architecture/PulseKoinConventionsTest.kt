package jp.kaleidot725.pulse.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.core.exception.KoAssertionFailedException
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * The Koin assertions this artifact publishes, run over an app that declares its Koin modules in the app module and
 * over the same app with modules declared elsewhere.
 */
class PulseKoinConventionsTest {
    /**
     * The app declares the only Koin module, and a feature that only resolves from Koin with `koinInject` is fine — the
     * module is found before the rule is checked, so the pass is not an empty one.
     */
    @Test
    fun `passes when Koin modules are declared in the app`() {
        assertEquals(listOf("AppModule"), conforming.pulseKoinModuleFiles().map { it.name })

        conforming.assertPulseKoinConventions()
    }

    /**
     * The demo declares its domain, data and feature modules side by side under `demo/app`, where the whole graph can
     * be read in one place.
     */
    @Test
    fun `passes on the demo application`() {
        val demo = Konsist.scopeFromDirectory("demo")

        assertEquals(
            setOf("DomainModule", "DataModule", "CountFeatureModule"),
            demo.pulseKoinModuleFiles().map { it.name }.toSet(),
        )
        demo.assertPulseKoinConventions()
    }

    /** A project that does not use Koin has nothing to check and passes. */
    @Test
    fun `passes on a project without Koin`() {
        Konsist.scopeFromDirectory(fixture("custom")).assertPulseKoinConventions()
    }

    /**
     * A data module declaring its own bindings with the DSL, and a feature declaring one with Koin Annotations, both
     * take part of the graph away from the app.
     */
    @Test
    fun `reports Koin modules declared outside the app`() {
        val failure = assertFailsWith<KoAssertionFailedException> { violating.assertKoinModulesLiveInTheApp() }
        val message = failure.message ?: ""

        listOf("DataKoinModule", "SettingsKoinModule").forEach { name ->
            check(message.contains(name)) { "Expected the failure to name $name, but it said: $message" }
        }
    }

    private fun fixture(path: String) = "library/architecture/src/jvmTest/resources/fixture/modules/$path"

    private val conforming get() = Konsist.scopeFromDirectory(fixture("conforming"))

    private val violating get() = conforming + Konsist.scopeFromDirectory(fixture("violating"))
}
