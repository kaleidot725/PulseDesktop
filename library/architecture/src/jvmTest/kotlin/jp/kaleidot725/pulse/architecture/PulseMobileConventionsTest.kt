package jp.kaleidot725.pulse.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.core.exception.KoAssertionFailedException
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * [assertPulseMobileConventions] over a mobile feature that follows it and features that break each of its own rules: a
 * screen with one ViewModel beside it, in place of the desktop's host and contents.
 */
class PulseMobileConventionsTest {
    /**
     * The mobile layout holds for code that follows it, and holds quietly: the screen is found at the root the ViewModel
     * marks, and the rules shared with the desktop pass on the same code.
     */
    @Test
    fun `passes on a mobile feature that follows the conventions`() {
        assertEquals(setOf("fixture.mobile.conforming.counter"), conforming.pulseMobileFeatureRoots())
        assertEquals(listOf("CounterScreen"), conforming.pulseScreens().map { it.name })

        conforming.assertPulseMobileConventions()
    }

    /**
     * The desktop layout is not a mobile one: its ViewModels sit under `content`, which on a phone only adds a layer
     * between the screen and the ViewModel it binds.
     */
    @Test
    fun `reports the desktop layout`() {
        assertFailsWith<KoAssertionFailedException> { desktop.assertMobileFeaturesHaveOneViewModel() }
    }

    /**
     * A project that has not adopted PulsePlatform anywhere must pass rather than fail on empty filters, since the
     * assertions are meant to be added before the code they check.
     */
    @Test
    fun `passes on a project with no PulsePlatform types`() {
        Konsist.scopeFromDirectory("library/architecture/src/jvmMain").assertPulseMobileConventions()
    }

    /**
     * One screen is one ViewModel. A second ViewModel in the package is a second screen that has not been given a feature
     * of its own, so both are reported.
     */
    @Test
    fun `reports a second ViewModel in a feature`() {
        val failure = assertFailsWith<KoAssertionFailedException> { violating("twin").assertMobileFeaturesHaveOneViewModel() }
        assertMessageMentions(failure, "FirstViewModel")
        assertMessageMentions(failure, "SecondViewModel")
    }

    /**
     * A ViewModel under `content` brings the desktop's layer back between the screen and the ViewModel it binds.
     */
    @Test
    fun `reports a ViewModel away from the root of its feature`() {
        val failure = assertFailsWith<KoAssertionFailedException> { violating("nested").assertMobileFeaturesHaveOneViewModel() }
        assertMessageMentions(failure, "NestedViewModel")
    }

    /**
     * The file name is how a reader finds what a route shows, so a composable at the root of a feature that is not
     * named `…Screen` is reported.
     */
    @Test
    fun `reports a screen that is not named Screen`() {
        val failure = assertFailsWith<KoAssertionFailedException> { violating("unbound").assertScreensBindTheirViewModel() }
        assertMessageMentions(failure, "UnboundPage")
    }

    /**
     * The screen is the only place the ViewModel and the UI meet, so a screen that never calls `PulseContent` leaves the
     * ViewModel unobserved and its `onSetup` never runs.
     */
    @Test
    fun `reports a screen that binds no ViewModel`() {
        val failure = assertFailsWith<KoAssertionFailedException> { violating("idle").assertScreensBindTheirViewModel() }
        assertMessageMentions(failure, "IdleScreen")
    }

    /**
     * A ViewModel with no screen beside it is either bound from another feature or not at all, and neither is a feature
     * a reader can find.
     */
    @Test
    fun `reports a ViewModel without a screen`() {
        val failure = assertFailsWith<KoAssertionFailedException> { violating("missing").assertScreensBindTheirViewModel() }
        assertMessageMentions(failure, "ScreenlessViewModel")
    }

    /**
     * `section` and `component` are the only packages under a mobile feature, so a package of its own, such as `text`, is
     * reported even when what it holds is not a composable.
     */
    @Test
    fun `reports a file in a mobile feature package without a role`() {
        val failure = assertFailsWith<KoAssertionFailedException> { violating("stray").assertMobileFeaturePackagesHaveARole() }
        assertMessageMentions(failure, "StrayLabels")
    }

    private fun assertMessageMentions(
        failure: KoAssertionFailedException,
        name: String,
    ) {
        val message = failure.message ?: ""
        check(message.contains(name)) { "Expected the failure to name $name, but it said: $message" }
    }

    private val conforming get() = Konsist.scopeFromDirectory("library/architecture/src/jvmTest/resources/fixture/mobile/conforming")

    private fun violating(feature: String) =
        Konsist.scopeFromDirectory("library/architecture/src/jvmTest/resources/fixture/mobile/violating/feature/$feature")

    private val desktop get() = Konsist.scopeFromDirectory("library/architecture/src/jvmTest/resources/fixture/conforming")
}
