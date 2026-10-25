package counter.app

import androidx.compose.runtime.Composable
import counter.core.common.Clock
import counter.core.data.repository.InMemoryCounterRepository
import counter.core.domain.usecase.GetCounterUseCase
import counter.feature.counter.CounterScreen
import counter.feature.settings.SettingsScreen

@Composable
fun CounterApp(clock: Clock) {
    CounterScreen(GetCounterUseCase(InMemoryCounterRepository(), clock))
    SettingsScreen()
}
