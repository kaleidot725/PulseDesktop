package jp.kaleidot725.pulse.demo.count

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jp.kaleidot725.pulse.demo.core.domain.model.PulseArea
import jp.kaleidot725.pulse.demo.core.domain.model.PulseAreaPosition
import jp.kaleidot725.pulse.demo.count.component.PulseCountHeader
import jp.kaleidot725.pulse.demo.count.section.area.PulseAreaContent
import jp.kaleidot725.pulse.demo.count.section.area.PulseAreaViewModel
import jp.kaleidot725.pulse.demo.count.state.PulseCountBroadcast
import jp.kaleidot725.pulse.mvi.PulseHost
import jp.kaleidot725.pulse.navigation.rememberPulseContainer
import jp.kaleidot725.pulse.navigation.rememberPulseViewModel
import kotlinx.coroutines.launch
import org.koin.compose.getKoin
import org.koin.core.parameter.parametersOf

@Composable
fun PulseCountHost(
    depth: Int,
    onNewArea: () -> Unit,
    onBack: (() -> Unit)?,
) {
    val koin = getKoin()
    val areas =
        PulseAreaPosition.entries.map { position ->
            rememberPulseViewModel(key = position.name) {
                koin.get<PulseAreaViewModel> { parametersOf(PulseArea(depth, position)) }
            }
        }
    val container = rememberPulseContainer { koin.get<PulseCountContainer> { parametersOf(areas) } }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    PulseHost(container = container) { onRefresh, onBroadcast ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PulseCountHeader(
                    depth = depth,
                    onNewArea = onNewArea,
                    onBack = onBack,
                    onReset = { onBroadcast(PulseCountBroadcast.Reset) },
                    onRefresh = onRefresh,
                )

                Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    areas.chunked(2).forEach { row ->
                        Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                            row.forEach { area ->
                                PulseAreaContent(
                                    viewModel = area,
                                    onCharged = { charged ->
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                "${charged.position.label} charged at ${charged.count}",
                                            )
                                        }
                                    },
                                    modifier = Modifier.weight(1f).fillMaxHeight(),
                                )
                            }
                        }
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}
