package jp.kaleidot725.pulse.demo.count.section.area.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.kaleidot725.pulse.demo.core.designsystem.component.PulseCard
import jp.kaleidot725.pulse.demo.core.domain.model.PulseAreaPosition
import jp.kaleidot725.pulse.demo.count.resource.label
import jp.kaleidot725.pulse.demo.count.resources.Res
import jp.kaleidot725.pulse.demo.count.resources.setup_count
import jp.kaleidot725.pulse.demo.count.section.area.state.PulseAreaState
import org.jetbrains.compose.resources.stringResource

@Composable
fun PulseAreaCell(
    state: PulseAreaState,
    onPulse: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PulseCard(
        onClick = onPulse,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(state.position.label),
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = state.count.toString(),
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(Res.string.setup_count, state.setupCount),
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Preview
@Composable
private fun PulseAreaCell_Preview() {
    PulseAreaCell(
        state = PulseAreaState(position = PulseAreaPosition.TopRight, count = 7),
        onPulse = {},
        modifier = Modifier.size(width = 320.dp, height = 220.dp),
    )
}
