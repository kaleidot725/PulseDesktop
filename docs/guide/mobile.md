# Mobile screens

`mvi-mobile` is PulsePlatform's MVI artifact for Compose Multiplatform on Android and iOS. A phone shows one screen at a time, and one ViewModel is enough for it, so a mobile feature is one **screen** that binds one ViewModel. There is no Container to coordinate several ViewModels, and the artifact leaves out what only a Container needs. The rules check this shape with [`assertPulseMobileConventions()`](/guide/rules/ui#mobile).

## What the artifact holds

The package is the same as on the desktop, `jp.kaleidot725.pulse.mvi`, and so is the behaviour of what is kept: `onSetup` runs the first time `PulseContent` observes the ViewModel, `cancel` and `close` end the work it started, and events are buffered until the UI collects them. `PulseViewModel` takes three type parameters instead of five, and has no `onReceive` or `unicast`.

| Kept | Left out |
|---|---|
| `PulseState`, `PulseAction`, `PulseEvent` | `PulseBroadcast`, `PulseUnicast` |
| `PulseViewModel<State, Action, Event>` | `PulseContainer` |
| `PulseContent` | `PulseHost` |

## A screen

The screen is what a route shows and the one place the ViewModel meets the UI. It creates the ViewModel with whatever creates ViewModels in the app — `viewModel()` from lifecycle-viewmodel-compose on Android and iOS alike, `koinViewModel()`, or a Navigation 3 entry's ViewModel decorator — and binds it with `PulseContent`. It hands the state and callbacks to its sections, and turns the events that leave the screen, such as going back, into callbacks of its own, so navigation stays with whoever shows the screen.

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

## How long a ViewModel lives

`PulseViewModel` is an `androidx.lifecycle.ViewModel`, so the `ViewModelStoreOwner` that holds it decides its lifetime, on Android and on iOS. `viewModel()` called in a screen keeps the ViewModel in the owner in scope: the activity or the navigation entry on Android, and the owner Compose provides on iOS. A configuration change on Android reuses the instance, so `onSetup` does not run again and the state stands. Leaving the screen for good clears the owner, and `onCleared` cancels the work the ViewModel started.

## Next steps

- [Mobile quick start](/guide/getting-started-mobile) — build and check a counter screen
- [UI structure rules](/guide/rules/ui#mobile) — the mobile layout, rule by rule
- [ViewModel](/guide/viewmodel) — state, actions, events and the lifecycle hooks
