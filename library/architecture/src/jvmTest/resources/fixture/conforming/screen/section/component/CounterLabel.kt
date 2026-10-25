package fixture.conforming.screen.section.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CounterLabel(count: Int) {
    CounterLabelText("$count")
}

@Composable
fun CounterBadge(count: Int?) {
    CounterLabelText(count?.toString() ?: "-")
}

@Composable
private fun CounterLabelText(text: String) {
    println(text)
}

@Preview
@Composable
private fun CounterLabel_Preview() {
    CounterLabel(count = 7)
}

@Preview
@Composable
private fun CounterBadge_Preview_Empty() {
    CounterBadge(count = null)
}
