package jp.kaleidot725.pulse.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.NavEntryDecorator
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

/**
 * The navigation3 composables under a real composition on Android: which owner holds a ViewModel, which key identifies
 * it, and what `rememberPulseNavEntryDecorators` hands to `NavDisplay`.
 */
@RunWith(RobolectricTestRunner::class)
class PulseNavigationTest {
    @get:Rule
    val composeRule = createComposeRule()

    /**
     * The instance lives in the owner's `ViewModelStore`, not in the composition, so recomposing finds what is already
     * there.
     */
    @Test
    fun `returns the same ViewModel while the owner stays the same`() {
        val owner = TestOwner()
        var tick by mutableIntStateOf(0)
        val viewModels = mutableListOf<NavViewModel>()

        composeRule.setContent {
            CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
                tick.let { viewModels += rememberPulseViewModel { NavViewModel() } }
            }
        }
        composeRule.waitForIdle()
        tick = 1
        composeRule.waitForIdle()

        assertEquals(2, viewModels.size)
        assertSame(viewModels.first(), viewModels.last())
    }

    /**
     * Without a key the type name identifies the instance, so two calls share it. A screen that needs two of one ViewModel
     * passes keys.
     */
    @Test
    fun `separates two instances of one type when each is given its own key`() {
        val owner = TestOwner()
        lateinit var unkeyedA: NavViewModel
        lateinit var unkeyedB: NavViewModel
        lateinit var left: NavViewModel
        lateinit var right: NavViewModel

        composeRule.setContent {
            CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
                unkeyedA = rememberPulseViewModel { NavViewModel() }
                unkeyedB = rememberPulseViewModel { NavViewModel() }
                left = rememberPulseViewModel(key = "left") { NavViewModel() }
                right = rememberPulseViewModel(key = "right") { NavViewModel() }
            }
        }
        composeRule.waitForIdle()

        assertSame(unkeyedA, unkeyedB)
        assertNotSame(left, right)
    }

    /**
     * Passing `entryDecorators` replaces the default saveable state holder decorator, and losing it silently throws away
     * `rememberSaveable` state — so both are returned together, in the order NavDisplay expects.
     */
    @Test
    fun `returns both NavEntry decorators, not only the ViewModel store one`() {
        lateinit var decorators: List<NavEntryDecorator<Any>>

        composeRule.setContent {
            decorators = rememberPulseNavEntryDecorators()
        }
        composeRule.waitForIdle()

        assertEquals(2, decorators.size)
    }
}
