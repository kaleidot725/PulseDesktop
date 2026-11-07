package jp.kaleidot725.pulse.mvi

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * What [PulseContent] hands its content in a real composition on iOS: the ViewModel's state and `onAction`, and one-time
 * setup with event delivery.
 */
@OptIn(ExperimentalTestApi::class)
class PulseContentIosTest {
    /**
     * This is the whole contract of the composable: the content never touches the ViewModel itself, and a dispatched action
     * comes back as the next state.
     */
    @Test
    fun `hands the content the state and an onAction that reaches the ViewModel`() =
        runComposeUiTest {
            val viewModel = ContentViewModel()
            var state = ContentState(count = -1)
            lateinit var onAction: (ContentAction) -> Unit

            setContent {
                PulseContent(viewModel = viewModel) { uiState, dispatch ->
                    state = uiState
                    onAction = dispatch
                }
            }
            assertEquals(ContentState(count = 0), state)

            onAction(ContentAction.Increment)
            waitForIdle()

            assertEquals(ContentState(count = 1), state)
        }

    /**
     * Setup belongs to the instance, not to the composition, and `onEvent` is re-read on every recomposition, so events
     * still arrive after the content changes.
     */
    @Test
    fun `sets the ViewModel up once and keeps delivering its events`() =
        runComposeUiTest {
            val viewModel = ContentViewModel()
            val received = mutableListOf<ContentEvent>()

            setContent {
                PulseContent(viewModel = viewModel, onEvent = { received += it }) { _, _ -> }
            }

            viewModel.event(ContentEvent.Ping("before"))
            viewModel.onAction(ContentAction.Increment)
            waitForIdle()
            viewModel.event(ContentEvent.Ping("after"))
            waitForIdle()

            assertEquals(1, viewModel.setupCount)
            assertEquals(listOf<ContentEvent>(ContentEvent.Ping("before"), ContentEvent.Ping("after")), received)
        }

    /**
     * The composable is useful with no content at all: the setup still runs, which is how a ViewModel can be started
     * from a place that draws nothing.
     */
    @Test
    fun `observes without rendering when content is left out`() =
        runComposeUiTest {
            val viewModel = ContentViewModel()

            setContent {
                PulseContent(viewModel = viewModel)
            }
            viewModel.event(ContentEvent.Ping("dropped"))
            waitForIdle()

            assertEquals(1, viewModel.setupCount)
        }
}
