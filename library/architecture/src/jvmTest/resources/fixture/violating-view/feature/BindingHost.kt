package fixture.violating.feature

import androidx.compose.runtime.Composable
import fixture.violating.CounterViewModel
import jp.kaleidot725.pulse.mvi.PulseContent

@Composable
fun BindingHost(viewModel: CounterViewModel) {
    PulseContent(viewModel = viewModel) { _, _ -> }
}
