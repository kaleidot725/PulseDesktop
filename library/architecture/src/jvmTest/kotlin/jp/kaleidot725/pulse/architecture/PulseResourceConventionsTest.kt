package jp.kaleidot725.pulse.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.core.exception.KoAssertionFailedException
import org.junit.Test
import kotlin.test.assertFailsWith

/**
 * The resource assertions this artifact publishes, run over a feature that reads its text from string resources and
 * over code that writes text into the domain and the composables.
 */
class PulseResourceConventionsTest {
    /**
     * Text from `stringResource`, a literal that only places values, a separator in the domain, and a literal in a
     * `@Preview` all pass, since none of them is text a translation would have to change.
     */
    @Test
    fun `passes on text read from string resources`() {
        conforming.assertPulseResourceConventions()
    }

    /** The demo reads its text from its own string resources, in English and in Japanese. */
    @Test
    fun `passes on the demo application`() {
        Konsist.scopeFromDirectory("demo").assertPulseResourceConventions()
    }

    /**
     * An enum that carries its display name in the domain is in one language for every screen, and translating it
     * means editing the domain.
     */
    @Test
    fun `reports text in the domain`() {
        assertReports("LabelledGreeting.kt") { assertDomainHoldsNoText() }
    }

    /**
     * A literal handed to `Text` or to a `title` argument cannot be translated, even when it also places a value, as
     * `"Count: $count"` does.
     */
    @Test
    fun `reports a composable that writes its text as a literal`() {
        assertReports("HardcodedCard") { assertComposablesTakeTextFromResources() }
        assertReports("HardcodedTitle") { assertComposablesTakeTextFromResources() }
    }

    private fun assertReports(
        name: String,
        assertion: KoScope.() -> Unit,
    ) {
        val failure = assertFailsWith<KoAssertionFailedException> { violating.assertion() }
        val message = failure.message ?: ""
        check(message.contains(name)) { "Expected the failure to name $name, but it said: $message" }
    }

    private fun fixture(path: String) = "library/architecture/src/jvmTest/resources/fixture/resources/$path"

    private val conforming get() = Konsist.scopeFromDirectory(fixture("conforming"))

    private val violating get() = Konsist.scopeFromDirectory(fixture("violating"))
}
