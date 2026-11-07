package fixture.mobile.conforming.counter.section

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import fixture.mobile.conforming.counter.component.CounterBadge

@Composable
fun CounterSection(
    count: Int,
    onIncrement: () -> Unit,
) {
    CounterBadge(count = count)
}

@Preview
@Composable
private fun CounterSection_Preview() {
    CounterSection(count = 3, onIncrement = {})
}
