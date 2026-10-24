package jp.kaleidot725.pulse.demo.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PulseViolet = Color(0xFF7C5CFF)
private val PulseVioletLight = Color(0xFFB9A8FF)

@Composable
fun PulseDesktopTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme =
            if (isSystemInDarkTheme()) {
                darkColorScheme(primary = PulseVioletLight)
            } else {
                lightColorScheme(primary = PulseViolet)
            },
        content = content,
    )
}
