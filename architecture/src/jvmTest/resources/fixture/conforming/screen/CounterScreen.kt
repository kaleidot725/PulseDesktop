package fixture.conforming.screen

import androidx.compose.runtime.Composable
import fixture.conforming.CounterViewModel
import fixture.conforming.repository.CounterRepository
import fixture.conforming.screen.section.CounterSection
import jp.kaleidot725.pulse.navigation.rememberPulseViewModel

@Composable
fun CounterScreen(repository: CounterRepository) {
    val viewModel = rememberPulseViewModel { CounterViewModel(repository) }
    CounterSection(viewModel = viewModel)
}
