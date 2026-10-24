package jp.kaleidot725.pulse.demo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import jp.kaleidot725.pulse.demo.core.data.DataStorePulseCountRepository
import java.io.File

fun main() {
    val directory = File(System.getProperty("user.home"), ".pulsedesktop/demo").apply { mkdirs() }
    val repository = DataStorePulseCountRepository.create(File(directory, "counts.preferences_pb").absolutePath)

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "PulseDesktop - Counter Demo",
        ) {
            DemoApp(repository = repository)
        }
    }
}
