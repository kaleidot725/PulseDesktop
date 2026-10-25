package counter.feature.counter

import androidx.compose.runtime.Composable
import counter.core.domain.GetCounterUseCase
import org.koin.compose.koinInject

@Composable
fun CounterRoute(getCounter: GetCounterUseCase = koinInject()) = CounterScreen(getCounter)
