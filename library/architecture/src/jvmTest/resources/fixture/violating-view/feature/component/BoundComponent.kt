package fixture.violating.feature.component

import androidx.compose.runtime.Composable
import fixture.violating.CounterViewModel
import jp.kaleidot725.pulse.mvi.PulseContent

@Composable
fun BoundComponent(viewModel: CounterViewModel) {
    PulseContent(viewModel = viewModel) { _, _ -> }
}
