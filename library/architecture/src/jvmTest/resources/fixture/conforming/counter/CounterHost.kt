package fixture.conforming.counter

import androidx.compose.runtime.Composable
import fixture.conforming.counter.content.total.TotalContent
import fixture.conforming.counter.content.total.TotalViewModel
import jp.kaleidot725.pulse.mvi.PulseHost

@Composable
fun CounterHost(viewModel: TotalViewModel) {
    PulseHost(container = CounterContainer(listOf(viewModel))) { _, _ ->
        TotalContent(viewModel = viewModel)
    }
}
