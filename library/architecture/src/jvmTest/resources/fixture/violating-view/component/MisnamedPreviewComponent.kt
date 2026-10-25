package fixture.violating.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MisnamedPreviewComponent(count: Int) {
    println(count)
}

@Preview
@Composable
private fun MisnamedPreviewComponentPreview() {
    MisnamedPreviewComponent(count = 1)
}
