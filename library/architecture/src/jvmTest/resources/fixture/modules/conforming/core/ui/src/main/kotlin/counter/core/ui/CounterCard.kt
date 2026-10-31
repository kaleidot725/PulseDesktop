package counter.core.ui

import androidx.compose.runtime.Composable
import counter.core.designsystem.button.PulseButton
import counter.core.domain.model.Counter

@Composable
fun CounterCard(counter: Counter) = PulseButton(counter.value.toString())
