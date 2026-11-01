package fixture.conforming.counter.content.total

import androidx.compose.runtime.Composable
import fixture.conforming.counter.content.total.section.TotalSection
import jp.kaleidot725.pulse.mvi.PulseContent

@Composable
fun TotalContent(viewModel: TotalViewModel) {
    PulseContent(viewModel = viewModel) { state, onAction ->
        TotalSection(state = state, onIncrement = { onAction(TotalAction.Increment) })
    }
}
