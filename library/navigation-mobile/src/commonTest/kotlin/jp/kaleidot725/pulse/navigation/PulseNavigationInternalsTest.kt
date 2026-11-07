package jp.kaleidot725.pulse.navigation

import jp.kaleidot725.pulse.mvi.PulseViewModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * The plain functions behind `rememberPulseViewModel`, tested without a composition on Android and iOS alike.
 */
class PulseNavigationInternalsTest {
    /**
     * The key identifies the instance inside its owner's `ViewModelStore` and `viewModel()` will not take null for it. A
     * top-level class has a qualified name and an anonymous object has none, hence the fallback.
     */
    @Test
    fun `defaultPulseKey falls back from the qualified name to the given name`() {
        val anonymous =
            object : PulseViewModel<NavState, NavAction, NavEvent>(NavState()) {
                override fun onAction(uiAction: NavAction) = Unit
            }

        assertEquals("jp.kaleidot725.pulse.navigation.NavViewModel", defaultPulseKey(NavViewModel::class, "x"))
        assertEquals("PulseViewModel", defaultPulseKey(anonymous::class, "PulseViewModel"))
    }

    /**
     * Calling `rememberPulseViewModel` where nothing provides an owner is an easy mistake, and the message has to name the
     * cause rather than read as a null pointer.
     */
    @Test
    fun `requirePulseViewModelStoreOwner says what is missing when no owner is in scope`() {
        val error = assertFailsWith<IllegalStateException> { requirePulseViewModelStoreOwner(null) }
        assertTrue(error.message!!.startsWith("No ViewModelStoreOwner in scope"))

        val owner = TestOwner()
        assertSame(owner, requirePulseViewModelStoreOwner(owner))
    }
}
