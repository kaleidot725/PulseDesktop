package jp.kaleidot725.pulse.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.core.exception.KoAssertionFailedException
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * The assertions this artifact publishes, run over code that follows the conventions and code that breaks each one.
 */
class PulseConventionsTest {
    /**
     * The conventions hold for code that follows them, and hold quietly: an assertion that passed here would be worth
     * nothing if it also passed on the violations below.
     */
    @Test
    fun `passes on code that follows the conventions`() {
        assertEquals(setOf("fixture.conforming.counter"), conforming.pulseFeatureRoots())
        assertEquals(listOf("CounterHost"), conforming.pulseHosts().map { it.name })
        assertEquals(listOf("TotalContent"), conforming.pulseContents().map { it.name })
        assertEquals(listOf("TotalSection"), conforming.pulseSections().map { it.name })
        assertEquals(listOf("CounterLabel"), conforming.pulseComponents().map { it.name })

        conforming.assertPulseMviConventions()
    }

    /**
     * The demo is the reference application, so the conventions are checked against it rather than only against
     * fixtures: a rule that no real code satisfies is a rule nobody will keep.
     */
    @Test
    fun `passes on the demo application`() {
        Konsist.scopeFromDirectory("demo").assertPulseMviConventions()
    }

    /**
     * A project that has not adopted PulseDesktop anywhere must pass rather than fail on empty filters, since the
     * assertions are meant to be added before the code they check.
     */
    @Test
    fun `passes on a project with no PulseDesktop types`() {
        Konsist.scopeFromDirectory("library/architecture/src/jvmMain").assertPulseMviConventions()
    }

    /**
     * A state without `data` is the mistake with the least visible consequence — the screen keeps working and only
     * recomposes more than it needs to — so it is the one worth catching mechanically.
     */
    @Test
    fun `reports a state that is not a data class`() {
        val failure = assertFailsWith<KoAssertionFailedException> { violating.assertStatesAreDataTypes() }
        assertMessageMentions(failure, "PlainState")
    }

    /**
     * An unsealed message type compiles, and every `when` over it then needs an `else` branch that quietly swallows the
     * cases added later. A sealed interface would seal the type just as well, but a sealed class, or a single data class
     * or data object for a message with one case, is what [assertMessagesAreSealedOrDataTypes] asks for, so the
     * interfaces and the class and object without `data` are reported by name. The conforming fixture's `Pinged` and
     * `Cleared` show the single data class and data object passing.
     */
    @Test
    fun `reports a message that is not a sealed class`() {
        val failure = assertFailsWith<KoAssertionFailedException> { violating.assertMessagesAreSealedOrDataTypes() }
        assertMessageMentions(failure, "OpenAction")
        assertMessageMentions(failure, "InterfaceEvent")
        assertMessageMentions(failure, "OpenBroadcast")
        assertMessageMentions(failure, "LoneUnicast")
    }

    /**
     * A case without `data` compares by identity, so two equal messages differ and a log prints a hash instead of the
     * value. Cases of a nested `sealed class` are checked as well, since that is where a forgotten `data` hides.
     */
    @Test
    fun `reports a message case that is not a data type`() {
        val failure = assertFailsWith<KoAssertionFailedException> { violatingCases.assertMessagesAreSealedOrDataTypes() }
        assertMessageMentions(failure, "PlainCase")
        assertMessageMentions(failure, "OpenCase")
        assertMessageMentions(failure, "NestedCase")
    }

    /**
     * A state or a message outside a `…Contracts.kt` file, or in one that no ViewModel or Container sits beside, is one
     * more place to look for what a content and its Container agree on; [assertStatesAndMessagesLiveInContracts] asks
     * for the file beside the class that uses them.
     */
    @Test
    fun `reports a state or a message outside a Contracts file beside its owner`() {
        val failure = assertFailsWith<KoAssertionFailedException> { violating.assertStatesAndMessagesLiveInContracts() }
        assertMessageMentions(failure, "CounterState")
        assertMessageMentions(failure, "CounterAction")
        assertMessageMentions(failure, "PlainState")
        assertMessageMentions(failure, "OrphanState")
    }

    /**
     * The name is how a reader tells a ViewModel from a Container, since both are `ViewModel` subclasses to the
     * compiler.
     */
    @Test
    fun `reports a ViewModel that is not named ViewModel`() {
        val failure =
            assertFailsWith<KoAssertionFailedException> {
                violating.assertViewModelsAndContainersAreNamedAfterTheirBase()
            }
        assertMessageMentions(failure, "Counter")
    }

    /**
     * A `var` beside the state is data the UI cannot observe, which is why it is reported even though it compiles.
     */
    @Test
    fun `reports a ViewModel with a var property`() {
        val failure = assertFailsWith<KoAssertionFailedException> { violating.assertViewModelsHoldTheirDataInTheirState() }
        assertMessageMentions(failure, "Counter")
    }

    /**
     * A ViewModel outside `content` is one the sections below could reach, and one a reader has to search the feature
     * for.
     */
    @Test
    fun `reports a ViewModel outside a content package`() {
        val failure = assertFailsWith<KoAssertionFailedException> { violatingViews.assertViewModelsLiveInContents() }
        assertMessageMentions(failure, "StrayViewModel")
    }

    /** A Container inside one content hides what coordinates the whole feature. */
    @Test
    fun `reports a Container away from the feature root`() {
        val failure = assertFailsWith<KoAssertionFailedException> { violatingViews.assertContainersLiveAtTheFeatureRoot() }
        assertMessageMentions(failure, "StrayContainer")
    }

    /**
     * The host is the one composable at the root of a feature, so its name says what it is, and binding a ViewModel
     * there is how it grows into the file that knows everything.
     */
    @Test
    fun `reports a host that is misnamed or binds a ViewModel`() {
        val misnamed = assertFailsWith<KoAssertionFailedException> { violatingViews.assertHostsComposeContents() }
        assertMessageMentions(misnamed, "MisnamedScreen")

        val binding =
            assertFailsWith<KoAssertionFailedException> {
                violatingViews.slice { it.name != "MisnamedScreen" }.assertHostsComposeContents()
            }
        assertMessageMentions(binding, "BindingHost")
    }

    /**
     * A composable in a package of the feature that is none of the four roles is one nobody placed; a composable that
     * only returns a value, such as the conforming fixture's `countLabel`, passes.
     */
    @Test
    fun `reports a composable in a feature without a role`() {
        val failure = assertFailsWith<KoAssertionFailedException> { violatingViews.assertViewsHaveARole() }
        assertMessageMentions(failure, "StrayView")
    }

    /**
     * A content that binds nothing is a section that ended up in the wrong package, and the mistake is invisible until
     * someone looks for where the ViewModel is read.
     */
    @Test
    fun `reports a content that binds no ViewModel`() {
        val failure = assertFailsWith<KoAssertionFailedException> { violatingViews.assertContentsBindAViewModel() }
        assertMessageMentions(failure, "SilentContent")
    }

    /**
     * A section or a component that takes a ViewModel cannot be previewed on its own, which is the whole reason it was
     * split out, and one that binds is a content in the wrong package.
     */
    @Test
    fun `reports a section or a component that is not pure`() {
        val failure = assertFailsWith<KoAssertionFailedException> { violatingViews.assertSectionsAndComponentsArePure() }
        assertMessageMentions(failure, "BoundSection")
        assertMessageMentions(failure, "BoundComponent")
    }

    /**
     * A section or a component with no preview cannot be looked at on its own, and one whose preview does not follow
     * `<Name>_Preview` is not tied to it — the next rename leaves the preview behind. A private helper composable and a
     * component previewed only under a condition, such as `CounterBadge_Preview_Empty`, both pass.
     */
    @Test
    fun `reports a section or a component without a matching preview`() {
        assertEquals(emptyList(), conformingPreviewless())

        val failure = assertFailsWith<KoAssertionFailedException> { violatingViews.assertSectionsAndComponentsHavePreviews() }
        assertMessageMentions(failure, "UnpreviewedSection")
        assertMessageMentions(failure, "UnpreviewedComponent")
        assertMessageMentions(failure, "MisnamedPreviewComponent")
    }

    private fun conformingPreviewless(): List<String> =
        runCatching { conforming.assertSectionsAndComponentsHavePreviews() }.exceptionOrNull()?.message?.let { listOf(it) } ?: emptyList()

    private fun assertMessageMentions(
        failure: KoAssertionFailedException,
        name: String,
    ) {
        val message = failure.message ?: ""
        check(message.contains(name)) { "Expected the failure to name $name, but it said: $message" }
    }

    private val conforming get() = Konsist.scopeFromDirectory("library/architecture/src/jvmTest/resources/fixture/conforming")

    private val violating get() = Konsist.scopeFromDirectory("library/architecture/src/jvmTest/resources/fixture/violating")

    private val violatingCases get() = Konsist.scopeFromFile("library/architecture/src/jvmTest/resources/fixture/violating/CaseAction.kt")

    private val violatingViews get() = Konsist.scopeFromDirectory("library/architecture/src/jvmTest/resources/fixture/violating-view")
}
