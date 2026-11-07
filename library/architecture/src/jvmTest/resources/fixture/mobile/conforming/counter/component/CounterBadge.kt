package fixture.mobile.conforming.counter.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CounterBadge(count: Int) {
    Text(text = "$count")
}

@Preview
@Composable
private fun CounterBadge_Preview() {
    CounterBadge(count = 3)
}
