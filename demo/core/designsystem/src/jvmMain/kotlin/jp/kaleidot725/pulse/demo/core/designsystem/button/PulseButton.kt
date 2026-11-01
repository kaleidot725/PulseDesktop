package jp.kaleidot725.pulse.demo.core.designsystem.button

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PulseButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(onClick = onClick) {
        Text(text)
    }
}

@Composable
fun PulseTextButton(
    text: String,
    onClick: () -> Unit,
) {
    TextButton(onClick = onClick) {
        Text(text)
    }
}

@Preview
@Composable
private fun PulseButton_Preview() {
    PulseButton(text = "New Area", onClick = {})
}

@Preview
@Composable
private fun PulseTextButton_Preview() {
    PulseTextButton(text = "Reset", onClick = {})
}
