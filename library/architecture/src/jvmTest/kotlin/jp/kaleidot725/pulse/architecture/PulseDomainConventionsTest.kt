package jp.kaleidot725.pulse.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.core.exception.KoAssertionFailedException
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * The repository assertions this artifact publishes, run over repositories that hand out their data the expected way
 * and over repositories that break each rule.
 */
class PulseDomainConventionsTest {
    /**
     * A `StateFlow` and a cold `Flow` to follow, a `Result<V, E>` to fetch once, a command that returns nothing, and
     * dependencies on another repository and on functions — every shape the rules allow, found before they are checked.
     */
    @Test
    fun `passes on repositories that hand out flows and results`() {
        assertEquals(listOf("InMemoryCartRepository"), conforming.pulseRepositoryClasses().map { it.name })
        assertEquals(
            setOf("CatalogRepository", "CartRepository"),
            conforming.pulseRepositoryInterfaces().map { it.name }.toSet(),
        )

        conforming.assertPulseDomainConventions()
    }

    /**
     * The demo's counts come out of DataStore as a `Flow` per area, and the writes are commands, so the demo passes —
     * after its repository is found.
     */
    @Test
    fun `passes on the demo application`() {
        val demo = Konsist.scopeFromDirectory("demo")

        assertEquals(listOf("PulseCountRepository"), demo.pulseRepositoryInterfaces().map { it.name })
        assertEquals(listOf("DataStorePulseCountRepository"), demo.pulseRepositoryClasses().map { it.name })
        demo.assertPulseDomainConventions()
    }

    /**
     * A `SharedFlow` drops what came before the collector, a public `MutableStateFlow` lets the caller write past the
     * repository, a plain value is stale once read, and a property without a written type cannot be told apart from
     * any of them.
     */
    @Test
    fun `reports a property that is not a flow or a state flow`() {
        val failure = failureOf(scope("violating-properties")) { assertRepositoriesExposeFlowsOrStateFlows() }

        assertMentions(failure, "HotRepository", "WritableRepository", "SnapshotRepository", "InferredRepository")
    }

    /**
     * A use case in its own package whose functions do real work — combining two flows, deciding over a value — passes,
     * and a private helper of one line is not counted against it.
     */
    @Test
    fun `passes on use cases that do work of their own`() {
        val useCases = useCaseScope("conforming")

        assertEquals(listOf("CountTotalUseCase"), useCases.pulseUseCases().map { it.name })
        useCases.assertPulseDomainConventions()
    }

    /**
     * A use case of one line only hands the call on, whether it is written as an expression or as a block, and the
     * ViewModel could have made that call itself.
     */
    @Test
    fun `reports a use case that is a single line`() {
        val failure = failureOf(useCaseScope("violating")) { assertUseCasesDoMoreThanOneLine() }

        assertMentions(failure, "PassThroughUseCase", "BlockPassThroughUseCase")
    }

    /** A use case outside a `usecase` package is mixed in with the entities or the repository interfaces. */
    @Test
    fun `reports a use case outside the usecase package`() {
        val failure = failureOf(useCaseScope("violating")) { assertUseCasesLiveInTheUseCasePackage() }

        assertMentions(failure, "MisplacedUseCase")
    }

    /**
     * A plain return value hides the failure, `kotlin.Result` hides which failure, and a function without a written
     * return type cannot be told apart from either.
     */
    @Test
    fun `reports a one-shot call that does not return a result`() {
        val failure = failureOf(scope("violating-functions")) { assertRepositoriesReturnResultsFromOneShotCalls() }

        assertMentions(failure, "PlainValueRepository", "StdlibResultRepository", "UntypedRepository")
    }

    private fun failureOf(
        scope: KoScope,
        assertion: KoScope.() -> Unit,
    ): String = assertFailsWith<KoAssertionFailedException> { scope.assertion() }.message ?: ""

    private fun assertMentions(
        message: String,
        vararg names: String,
    ) {
        names.forEach { name -> check(message.contains(name)) { "Expected the failure to name $name, but it said: $message" } }
    }

    private fun scope(name: String) = Konsist.scopeFromDirectory("library/architecture/src/jvmTest/resources/fixture/repository/$name")

    private fun useCaseScope(name: String) = Konsist.scopeFromDirectory("library/architecture/src/jvmTest/resources/fixture/usecase/$name")

    private val conforming get() = scope("conforming")
}
