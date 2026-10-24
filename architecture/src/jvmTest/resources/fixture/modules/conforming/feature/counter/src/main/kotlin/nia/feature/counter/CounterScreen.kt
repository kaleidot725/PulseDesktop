package nia.feature.counter

import androidx.compose.runtime.Composable
import nia.core.domain.GetCounterUseCase
import nia.core.ui.CounterCard

@Composable
fun CounterScreen(getCounter: GetCounterUseCase) = CounterCard(getCounter())
