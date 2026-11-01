# Android

`mvi-android` is the `mvi` artifact for Jetpack Compose on Android. A phone shows one screen at a
time, and one ViewModel is usually enough for it, so this artifact keeps only what one ViewModel
needs.

## What the artifact holds

| Kept | Left out |
|---|---|
| `PulseState`, `PulseAction`, `PulseEvent` | `PulseBroadcast`, `PulseUnicast` |
| `PulseViewModel<State, Action, Event>` | `PulseContainer` |
| `PulseContent` | `PulseHost` |

The package is the same, `jp.kaleidot725.pulse.mvi`, and so is the behaviour of what is kept:

- `onSetup` runs the first time `PulseContent` observes the ViewModel
- `cancel` and `close` end the work it started
- Events are buffered until the UI collects them

`PulseViewModel` has three type parameters instead of five, and has no `onReceive` or `unicast`.

## Installation

Add JitPack to `settings.gradle.kts` as for the desktop artifacts, then depend on `mvi-android`
from the Android module. It needs `minSdk` 26 or higher.

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.github.kaleidot725.PulseDesktop:mvi-android:<version>")
}
```

## A screen

A screen creates its ViewModel with whatever creates ViewModels in the app, such as `viewModel()`,
`koinViewModel()` or a Navigation 3 entry's ViewModel decorator. It binds the ViewModel with
`PulseContent`. Events that leave the screen, such as going back, become callbacks of the screen.

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
        CounterSection(count = state.count, onIncrement = { onAction(CounterAction.Increment) })
    }
}
```

## Architecture checks

The `architecture` artifact works from Android unit tests too. It reads the sources with Konsist,
and the classes keep their package, so the checks recognise `PulseViewModel` as they do on the
desktop. The checks for `PulseContainer`, Broadcast and Unicast simply find nothing to check.
