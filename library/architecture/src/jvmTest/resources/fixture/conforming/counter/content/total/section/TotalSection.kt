package fixture.conforming.counter.content.total.section

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import fixture.conforming.counter.component.CounterLabel
import fixture.conforming.counter.content.total.TotalState

@Composable
fun TotalSection(
    state: TotalState,
    onIncrement: () -> Unit,
) {
    CounterLabel(count = state.count)
}

@Preview
@Composable
private fun TotalSection_Preview() {
    TotalSection(state = TotalState(count = 3), onIncrement = {})
}
