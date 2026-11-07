package jp.kaleidot725.pulse.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState

/**
 * Binds a [PulseViewModel] to the composition.
 *
 * Hands [content] the ViewModel's state and its `onAction`, sets the ViewModel up the first time it is
 * observed, and delivers its events to [onEvent], which is re-read on every recomposition.
 */
@Composable
public fun <State : PulseState, Action : PulseAction, Event : PulseEvent> PulseContent(
    viewModel: PulseViewModel<State, Action, Event>,
    onEvent: (Event) -> Unit = {},
    content: @Composable ((State, ((Action) -> Unit)) -> Unit) = { _, _ -> },
) {
    val state by viewModel.state.collectAsState()
    val onAction = viewModel::onAction
    val currentOnEvent by rememberUpdatedState(onEvent)
    LaunchedEffect(viewModel) {
        viewModel.setupOnce()
        viewModel.event.collect { currentOnEvent(it) }
    }

    content(state, onAction)
}
