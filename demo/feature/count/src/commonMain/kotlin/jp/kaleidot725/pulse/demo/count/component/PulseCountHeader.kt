package jp.kaleidot725.pulse.demo.count.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.kaleidot725.pulse.demo.core.designsystem.component.PulseButton
import jp.kaleidot725.pulse.demo.core.designsystem.component.PulseTextButton
import jp.kaleidot725.pulse.demo.count.resources.Res
import jp.kaleidot725.pulse.demo.count.resources.area_description
import jp.kaleidot725.pulse.demo.count.resources.area_title
import jp.kaleidot725.pulse.demo.count.resources.back
import jp.kaleidot725.pulse.demo.count.resources.new_area
import jp.kaleidot725.pulse.demo.count.resources.refresh_view
import jp.kaleidot725.pulse.demo.count.resources.reset
import org.jetbrains.compose.resources.stringResource

@Composable
fun PulseCountHeader(
    depth: Int,
    onNewArea: () -> Unit,
    onBack: (() -> Unit)?,
    onReset: () -> Unit,
    onRefresh: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = stringResource(Res.string.area_title, depth),
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = stringResource(Res.string.area_description),
            style = MaterialTheme.typography.bodyMedium,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PulseButton(text = stringResource(Res.string.new_area), onClick = onNewArea)
            if (onBack != null) {
                PulseTextButton(text = stringResource(Res.string.back), onClick = onBack)
            }
            PulseTextButton(text = stringResource(Res.string.reset), onClick = onReset)
            PulseTextButton(text = stringResource(Res.string.refresh_view), onClick = onRefresh)
        }
    }
}

@Preview
@Composable
private fun PulseCountHeader_Preview() {
    PulseCountHeader(depth = 2, onNewArea = {}, onBack = {}, onReset = {}, onRefresh = {})
}

@Preview
@Composable
private fun PulseCountHeader_Preview_FirstArea() {
    PulseCountHeader(depth = 1, onNewArea = {}, onBack = null, onReset = {}, onRefresh = {})
}
