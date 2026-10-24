package counter.core.ui

import androidx.compose.runtime.Composable
import counter.core.designsystem.PulseButton
import counter.core.domain.Counter

@Composable
fun CounterCard(counter: Counter) = PulseButton(counter.value.toString())
