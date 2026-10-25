package jp.kaleidot725.pulse.demo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import jp.kaleidot725.pulse.demo.di.countFeatureModule
import jp.kaleidot725.pulse.demo.di.dataModule
import jp.kaleidot725.pulse.demo.di.domainModule
import org.koin.core.context.startKoin
import java.io.File

fun main() {
    val directory = File(System.getProperty("user.home"), ".pulsedesktop/demo").apply { mkdirs() }

    startKoin {
        modules(
            domainModule,
            dataModule(countsFile = File(directory, "counts.preferences_pb").absolutePath),
            countFeatureModule,
        )
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "PulseDesktop - Counter Demo",
        ) {
            DemoApp()
        }
    }
}
