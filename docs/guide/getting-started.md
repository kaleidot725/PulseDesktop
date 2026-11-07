# Desktop quick start

This guide builds a counter window with Compose Desktop: a host, one content and a Container, checked by the rules at the end. For Android and iOS, see the [mobile quick start](/guide/getting-started-mobile).

It creates the ViewModel with `rememberPulseViewModel`, which the `navigation` artifact adds, so that
one goes in alongside `mvi`, and the rules go in the tests. To drive the lifecycle yourself with `mvi` alone, see
[ViewModel](/guide/viewmodel).

```kotlin
dependencies {
    implementation("com.github.kaleidot725.PulsePlatform:mvi:<version>")
    implementation("com.github.kaleidot725.PulsePlatform:navigation:<version>")
    testImplementation("com.github.kaleidot725.PulsePlatform:architecture:<version>")
}
```

## 1. Define State, Action, Event, Broadcast, and Unicast

Start by defining the five types that describe your feature. They live in Contracts files beside
the class that uses them: State, Action and Event in `content/counter/CounterContentContracts.kt`
beside the ViewModel, and Broadcast and Unicast in `CounterContracts.kt` beside the Container. They
are shown together here:

```kotlin
// State: the UI data rendered by your Composable
data class CounterState(val count: Int = 0) : PulseState

// Action: intents dispatched by the user
sealed class CounterAction : PulseAction {
    data object Increment : CounterAction()
    data object Decrement : CounterAction()
    data object Reset : CounterAction()
}

// Event: one-time side effects (navigation, snackbar, etc.)
sealed class CounterEvent : PulseEvent {
    data class ShowMessage(val message: String) : CounterEvent()
}

// Broadcast: messages sent from Container to all ViewModels
sealed class CounterBroadcast : PulseBroadcast {
    data object Refresh : CounterBroadcast()
}

// Unicast: messages sent from ViewModel to Container
sealed class CounterUnicast : PulseUnicast {
    data object ResetRequested : CounterUnicast()
}
```

## 2. Create a ViewModel

`PulseViewModel` manages its own UI state. Override the lifecycle hooks to handle actions and broadcasts:

```kotlin
class CounterViewModel(
    private val repository: CounterRepository,
) : PulseViewModel<CounterState, CounterAction, CounterEvent, CounterBroadcast, CounterUnicast>(
    initialUiState = CounterState(),
) {
    // Called once, by PulseContent, the first time it observes the ViewModel
    override fun onSetup() {
        coroutineScope.launch {
            repository.count.collect { count ->
                update { copy(count = count) }
                if (count != 0 && count % 10 == 0) {
                    event(CounterEvent.ShowMessage("$count reached!"))
                }
            }
        }
    }

    // Called when the user dispatches an action
    override fun onAction(uiAction: CounterAction) {
        coroutineScope.launch {
            when (uiAction) {
                CounterAction.Increment -> repository.increment()
                CounterAction.Decrement -> repository.decrement()
                CounterAction.Reset -> repository.reset()
            }
        }
    }

    // Called when the Container broadcasts a message
    override fun onReceive(broadcast: CounterBroadcast) {
        when (broadcast) {
            CounterBroadcast.Refresh ->
                event(CounterEvent.ShowMessage("Refreshed!"))
        }
    }
}
```

## 3. Create a Container

`PulseContainer` takes a list of ViewModels and lets you broadcast to all of them or refresh the view:

```kotlin
class CounterContainer(
    viewModels: List<PulseViewModel<*, *, *, CounterBroadcast, CounterUnicast>>,
) : PulseContainer<CounterBroadcast, CounterUnicast>(viewModels = viewModels)
```

## 4. Connect to Compose UI

### Entry point

Create the ViewModel and Container once at the top level:

```kotlin
fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Counter") {
        MaterialTheme {
            val viewModel = rememberPulseViewModel { CounterViewModel(CounterRepository()) }
            val container = rememberPulseContainer { CounterContainer(viewModels = listOf(viewModel)) }

            CounterHost(container = container, viewModel = viewModel)
        }
    }
}
```

### Host composable

The host is what the route shows. It sits at the root of the feature package in a file named
`CounterHost.kt`, beside `CounterContainer`, wraps the layout with `PulseHost` to enable refresh and
broadcast, and places the contents inside:

```kotlin
@Composable
fun CounterHost(container: CounterContainer, viewModel: CounterViewModel) {
    PulseHost(container = container) { onRefresh, onBroadcast ->
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(modifier = Modifier.align(Alignment.TopEnd)) {
                Button(onClick = { onRefresh() }) {
                    Text("Refresh View")
                }
                Button(onClick = { onBroadcast(CounterBroadcast.Refresh) }) {
                    Text("Send Broadcast")
                }
            }
            CounterContent(
                viewModel = viewModel,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}
```

### Content composable

A content is the part one ViewModel answers for, and it lives in `content/counter`, beside
`CounterViewModel`. It binds that ViewModel with `PulseContent`, which observes the state and
handles the events:

```kotlin
@Composable
fun CounterContent(viewModel: CounterViewModel, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = modifier) {
        PulseContent(
            viewModel = viewModel,
            onEvent = { event ->
                when (event) {
                    is CounterEvent.ShowMessage ->
                        scope.launch { snackbarHostState.showSnackbar(event.message) }
                }
            },
        ) { state, onAction ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "${state.count}", fontSize = 72.sp)
                Row {
                    Button(onClick = { onAction(CounterAction.Decrement) }) { Text("−") }
                    Button(onClick = { onAction(CounterAction.Increment) }) { Text("+") }
                }
                OutlinedButton(onClick = { onAction(CounterAction.Reset) }) { Text("Reset") }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}
```

## 5. Scope the ViewModel to a Navigation 3 destination

Step 4 created the ViewModel at the top level, where it lives as long as the window. Created inside
a Navigation 3 destination instead, it lives exactly as long as that route is on the back stack.

Two things make that happen.

- `NavDisplay` gets `rememberPulseNavEntryDecorators()` as its `entryDecorators`. That gives every
  back stack entry its own `ViewModelStoreOwner`
- The ViewModel and Container are created inside the destination, not above `NavDisplay`

How this works is covered in [Navigation 3](/guide/navigation3).

```kotlin
sealed interface Route : NavKey {
    data object Counter : Route
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Counter") {
        MaterialTheme {
            val backStack = remember { mutableStateListOf<Route>(Route.Counter) }

            NavDisplay(
                backStack = backStack,
                onBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) },
                entryDecorators = rememberPulseNavEntryDecorators(),
                entryProvider =
                    entryProvider {
                        entry<Route.Counter> {
                            val viewModel = rememberPulseViewModel { CounterViewModel(CounterRepository()) }
                            val container = rememberPulseContainer { CounterContainer(viewModels = listOf(viewModel)) }

                            CounterHost(container = container, viewModel = viewModel)
                        }
                    },
            )
        }
    }
}
```

## 6. Check the rules

One test runs the rules over the whole project. It passes when the files sit where step 1 and step 4 put them: the host and the Container at the feature root, the content and its ViewModel in `content/counter`. Run it with `./gradlew test`. A failure names the file and says what to change. This counter writes its labels as literals to stay short, so it runs the UI structure rules only; a real app adds the [other groups](/guide/rules/), such as the text rules that move those labels into string resources.

```kotlin
class PulsePlatformConventionTest {
    @Test
    fun `follows the PulsePlatform conventions`() {
        val scope = Konsist.scopeFromProduction()
        scope.assertPulseMviConventions()
    }
}
```

## Running the Demo

The repository includes a pulse grid demo: four areas share one Container, and a tap on one spreads
to the two it shares an edge with. It is split into an app, a feature, and a domain, data and design
system under `core`, wired with Koin, and keeps its counts in a DataStore — the layout the
[rules](/guide/rules/) describe, which it passes. Clone the repo and run:

```bash
./gradlew :demo:app:run
```

## Next Steps

- [UI structure rules](/guide/rules/ui) — every rule the test checks
- [MVI](/guide/mvi) — understand the data flow in depth
- [ViewModel](/guide/viewmodel) — advanced ViewModel patterns
- [Host and Container](/guide/container) — coordinating multiple ViewModels
- [Navigation 3](/guide/navigation3) — how back stack scoped lifetimes work
