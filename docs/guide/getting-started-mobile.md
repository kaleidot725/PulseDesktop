# Mobile quick start

This guide builds a counter screen for Android and iOS with `mvi-mobile`, and checks it with the rules. It assumes a Compose Multiplatform project with Android and iOS targets.

## 1. Add the artifacts

`mvi-mobile` goes in `commonMain`, and Gradle picks the Android or iOS variant for each target. The rules run on the JVM, so they go in the Android unit tests. `lifecycle-viewmodel-compose` gives `viewModel()` on both platforms.

```kotlin
// build.gradle.kts
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.github.kaleidot725.PulsePlatform:mvi-mobile:<version>")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
        }
    }
}

dependencies {
    testImplementation("com.github.kaleidot725.PulsePlatform:architecture:<version>")
}
```

## 2. Lay out the feature

A mobile feature is one screen and one ViewModel in one package, with `section` and `component` under it. The rules check this layout, so it is worth creating first.

```
counter/
├── CounterScreen.kt        # binds CounterViewModel with PulseContent
├── CounterViewModel.kt
├── CounterContracts.kt     # the ViewModel's state, actions and events
└── section/
    └── CounterSection.kt   # draws the state, takes callbacks
```

## 3. Define the contracts

The state is one `data class`. Actions and events are sealed classes with `data` cases. All three live in `CounterContracts.kt`, beside the ViewModel.

```kotlin
data class CounterState(val count: Int = 0) : PulseState

sealed class CounterAction : PulseAction {
    data object Increment : CounterAction()
    data object Back : CounterAction()
}

sealed class CounterEvent : PulseEvent {
    data object Back : CounterEvent()
}
```

## 4. Write the ViewModel

`PulseViewModel` takes three type parameters on mobile. `onAction` handles what the user does, `update` produces the next state, and `event` sends a one-time effect such as going back. `onSetup` runs once, the first time the screen observes the ViewModel, and is where a repository's flow would be collected.

```kotlin
class CounterViewModel : PulseViewModel<CounterState, CounterAction, CounterEvent>(
    initialUiState = CounterState(),
) {
    override fun onAction(uiAction: CounterAction) {
        when (uiAction) {
            CounterAction.Increment -> update { copy(count = count + 1) }
            CounterAction.Back -> event(CounterEvent.Back)
        }
    }
}
```

## 5. Write the section and the screen

The section draws the state and takes callbacks only, so it has a preview of its own. Its text comes from string resources, as the rules ask. The screen creates the ViewModel, binds it with `PulseContent`, hands the state to the section, and turns the events that leave the screen into its own callbacks.

```kotlin
@Composable
fun CounterSection(count: Int, onIncrement: () -> Unit, onBack: () -> Unit) {
    Column {
        Text(text = "$count")
        Button(onClick = onIncrement) { Text(text = stringResource(Res.string.increment)) }
        TextButton(onClick = onBack) { Text(text = stringResource(Res.string.back)) }
    }
}

@Preview
@Composable
private fun CounterSection_Preview() {
    CounterSection(count = 3, onIncrement = {}, onBack = {})
}
```

```kotlin
@Composable
fun CounterScreen(onBack: () -> Unit) {
    val viewModel = viewModel { CounterViewModel() }
    PulseContent(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                CounterEvent.Back -> onBack()
            }
        },
    ) { state, onAction ->
        CounterSection(
            count = state.count,
            onIncrement = { onAction(CounterAction.Increment) },
            onBack = { onAction(CounterAction.Back) },
        )
    }
}
```

## 6. Check the rules

One test in the Android unit tests runs the rules over the whole project, `commonMain` and `iosMain` included. Run it with `./gradlew testDebugUnitTest`. A failure names the file and says what to change.

```kotlin
class PulsePlatformConventionTest {
    @Test
    fun `follows the PulsePlatform conventions`() {
        val scope = Konsist.scopeFromProduction()
        scope.assertPulseMobileConventions()
        scope.assertPulseResourceConventions()
    }
}
```

## Next steps

- [Mobile screens](/guide/mobile) — what `mvi-mobile` keeps and how a screen's ViewModel lives
- [UI structure rules](/guide/rules/ui#mobile) — the mobile layout, rule by rule
- [Building with AI agents](/guide/ai-agents) — let an agent run the same test
