package counter.core.designsystem

import androidx.compose.runtime.Composable
import counter.core.domain.model.Counter

@Composable
fun DesignSystemKnowsTheDomain(counter: Counter) = PulseButton(counter.value.toString())
