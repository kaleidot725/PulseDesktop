package greeter.feature.greet

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun HardcodedCard(count: Int) {
    Text("Count: $count")
}

@Composable
fun HardcodedTitle() {
    Header(title = "Settings")
}

@Composable
private fun Header(title: String) = Text(title)
