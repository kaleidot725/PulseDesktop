package jp.kaleidot725.pulse.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.core.exception.KoAssertionFailedException
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * The domain assertions this artifact publishes, run over repositories that follow the conventions and repositories that
 * break each one.
 */
class PulseDomainConventionsTest {
    /**
     * An interface with an in-memory implementation, kept in a `repository` package and handed to the ViewModel by the
     * screen, is the shape every check below expects.
     */
    @Test
    fun `passes on repositories that follow the conventions`() {
        assertEquals(listOf("InMemoryCounterRepository"), conforming.pulseRepositoryClasses().map { it.name })
        assertEquals(listOf("CounterRepository"), conforming.pulseRepositoryInterfaces().map { it.name })

        conforming.assertPulseDomainConventions()
    }

    /**
     * The demo keeps its state in its ViewModels and has no repository yet, which the checks must accept: they are
     * meant to be in place before the first repository is written.
     */
    @Test
    fun `passes on the demo application`() {
        Konsist.scopeFromProject(moduleName = "demo").assertPulseDomainConventions()
    }

    /**
     * A repository outside a `repository` package is one the next screen will not find, and write again.
     */
    @Test
    fun `reports a repository outside a repository package`() {
        val failure =
            assertFailsWith<KoAssertionFailedException> { violating.assertRepositoriesLiveInARepositoryPackage() }
        assertMessageMentions(failure, "MisplacedRepository")
    }

    /**
     * A repository that imports Compose can only be tested and reused together with the UI it was meant to outlive.
     */
    @Test
    fun `reports a repository that depends on the UI`() {
        val failure = assertFailsWith<KoAssertionFailedException> { violating.assertRepositoriesDoNotDependOnTheUi() }
        assertMessageMentions(failure, "ComposeRepository")
    }

    /**
     * A section holding a repository has a second way to change the data, one its ViewModel never sees.
     */
    @Test
    fun `reports a section that takes a repository`() {
        val failure = assertFailsWith<KoAssertionFailedException> { violating.assertOnlyScreensTakeRepositories() }
        assertMessageMentions(failure, "RepositorySection")
    }

    /**
     * A public `MutableStateFlow`, a public mutable collection or a `var` each lets a caller change the data past the
     * repository's own rules. The collection is caught from its initializer, since it has no type written out.
     */
    @Test
    fun `reports a repository that exposes mutable state`() {
        val failure = assertFailsWith<KoAssertionFailedException> { violating.assertRepositoriesExposeReadOnlyState() }
        assertMessageMentions(failure, "LeakyRepository")
        assertMessageMentions(failure, "ListRepository")
        assertMessageMentions(failure, "WritableRepository")
    }

    private fun assertMessageMentions(
        failure: KoAssertionFailedException,
        name: String,
    ) {
        val message = failure.message ?: ""
        check(message.contains(name)) { "Expected the failure to name $name, but it said: $message" }
    }

    private val conforming get() = Konsist.scopeFromDirectory("architecture/src/jvmTest/resources/fixture/conforming")

    private val violating get() =
        Konsist.scopeFromDirectory("architecture/src/jvmTest/resources/fixture/violating-domain")
}
