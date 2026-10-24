package nia.app

import androidx.compose.runtime.Composable
import nia.core.common.Clock
import nia.core.data.OfflineCounterRepository
import nia.core.database.CounterDao
import nia.core.domain.GetCounterUseCase
import nia.feature.counter.CounterScreen
import nia.feature.settings.SettingsScreen

@Composable
fun NiaApp(dao: CounterDao, clock: Clock) {
    CounterScreen(GetCounterUseCase(OfflineCounterRepository(dao), clock))
    SettingsScreen()
}
