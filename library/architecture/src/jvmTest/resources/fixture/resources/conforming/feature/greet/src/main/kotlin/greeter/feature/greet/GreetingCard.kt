package greeter.feature.greet

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import greeter.core.domain.model.Greeting
import org.jetbrains.compose.resources.stringResource

@Composable
fun GreetingCard(greeting: Greeting) {
    Text(stringResource(Res.string.greeting_title))
    Text(text = "${greeting.count}")
    Text("${greeting.count} / ${greeting.key}")
}

@Preview
@Composable
private fun GreetingCard_Preview() {
    Text("Sample greeting")
}
