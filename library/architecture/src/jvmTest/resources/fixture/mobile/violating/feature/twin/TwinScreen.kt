package fixture.mobile.violating.feature.twin

import androidx.compose.runtime.Composable
import jp.kaleidot725.pulse.mvi.PulseContent

@Composable
fun TwinScreen(viewModel: FirstViewModel) {
    PulseContent(viewModel = viewModel)
}
