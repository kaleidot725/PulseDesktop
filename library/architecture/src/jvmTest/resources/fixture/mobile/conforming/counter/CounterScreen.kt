package fixture.mobile.conforming.counter

import androidx.compose.runtime.Composable
import fixture.mobile.conforming.counter.section.CounterSection
import jp.kaleidot725.pulse.mvi.PulseContent

@Composable
fun CounterScreen(
    viewModel: CounterViewModel,
    onBack: () -> Unit,
) {
    PulseContent(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                CounterEvent.Back -> onBack()
            }
        },
    ) { state, onAction ->
        CounterSection(count = state.count, onIncrement = { onAction(CounterAction.Increment) })
    }
}
