package jp.kaleidot725.pulse.demo.count.content.area

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jp.kaleidot725.pulse.demo.count.content.area.section.PulseAreaSection
import jp.kaleidot725.pulse.mvi.PulseContent

@Composable
fun PulseAreaContent(
    viewModel: PulseAreaViewModel,
    onCharged: (PulseAreaEvent.Charged) -> Unit,
    modifier: Modifier = Modifier,
) {
    PulseContent(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                is PulseAreaEvent.Charged -> onCharged(event)
            }
        },
    ) { state, onAction ->
        PulseAreaSection(
            state = state,
            onAction = onAction,
            modifier = modifier,
        )
    }
}
