package counter.feature.counter

import androidx.compose.runtime.Composable
import counter.core.domain.GetCounterUseCase
import counter.core.ui.CounterCard

@Composable
fun CounterScreen(getCounter: GetCounterUseCase) = CounterCard(getCounter())
