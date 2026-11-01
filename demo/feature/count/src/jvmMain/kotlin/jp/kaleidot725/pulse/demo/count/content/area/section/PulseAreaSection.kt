package jp.kaleidot725.pulse.demo.count.content.area.section

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import jp.kaleidot725.pulse.demo.core.domain.model.PulseAreaPosition
import jp.kaleidot725.pulse.demo.count.content.area.PulseAreaAction
import jp.kaleidot725.pulse.demo.count.content.area.PulseAreaState
import jp.kaleidot725.pulse.demo.count.content.area.component.PulseAreaCell

@Composable
fun PulseAreaSection(
    state: PulseAreaState,
    onAction: (PulseAreaAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    PulseAreaCell(
        state = state,
        onPulse = { onAction(PulseAreaAction.Pulse) },
        modifier = modifier,
    )
}

@Preview
@Composable
private fun PulseAreaSection_Preview() {
    PulseAreaSection(
        state = PulseAreaState(position = PulseAreaPosition.TopLeft, count = 7),
        onAction = {},
    )
}
