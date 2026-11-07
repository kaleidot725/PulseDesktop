package fixture.mobile.violating.feature.stray

import androidx.compose.runtime.Composable
import jp.kaleidot725.pulse.mvi.PulseContent

@Composable
fun StrayScreen(viewModel: StrayViewModel) {
    PulseContent(viewModel = viewModel)
}
