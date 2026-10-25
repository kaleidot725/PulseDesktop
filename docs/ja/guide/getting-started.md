# はじめかた

このガイドでは、PulseDesktop でシンプルなカウンターアプリを作る手順を追います。

ViewModel の生成には `navigation` が追加する `rememberPulseViewModel` を使うので、`mvi` と一緒に追加してください。`mvi` だけでライフサイクルを自前で扱う場合は [ViewModel](/ja/guide/viewmodel) を参照してください。

```kotlin
dependencies {
    implementation("com.github.kaleidot725.PulseDesktop:mvi:<version>")
    implementation("com.github.kaleidot725.PulseDesktop:navigation:<version>")
}
```

## 1. State・Action・Event・Broadcast・Unicast を定義する

まず、機能を表す 5 つの型を定義します。

```kotlin
// State: Composable が描画する UI データ
data class CounterState(val count: Int = 0) : PulseState

// Action: ユーザーが発行する意図
sealed class CounterAction : PulseAction {
    data object Increment : CounterAction()
    data object Decrement : CounterAction()
    data object Reset : CounterAction()
}

// Event: 一度きりの副作用（ナビゲーション、スナックバーなど）
sealed class CounterEvent : PulseEvent {
    data class ShowMessage(val message: String) : CounterEvent()
}

// Broadcast: Container からすべての ViewModel へ送るメッセージ
sealed class CounterBroadcast : PulseBroadcast {
    data object Refresh : CounterBroadcast()
}

// Unicast: ViewModel から Container へ送るメッセージ
sealed interface CounterUnicast : PulseUnicast {
    data object ResetRequested : CounterUnicast
}
```

## 2. ViewModel を作る

`PulseViewModel` は自身の UI 状態を管理します。ライフサイクルフックをオーバーライドして、Action と Broadcast を処理します。

```kotlin
class CounterViewModel(
    private val repository: CounterRepository,
) : PulseViewModel<CounterState, CounterAction, CounterEvent, CounterBroadcast, CounterUnicast>(
    initialUiState = CounterState(),
) {
    // PulseContent がこの ViewModel を最初に観測したときに一度だけ呼ばれる
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

    // ユーザーが Action を発行したときに呼ばれる
    override fun onAction(uiAction: CounterAction) {
        coroutineScope.launch {
            when (uiAction) {
                CounterAction.Increment -> repository.increment()
                CounterAction.Decrement -> repository.decrement()
                CounterAction.Reset -> repository.reset()
            }
        }
    }

    // Container がメッセージを Broadcast したときに呼ばれる
    override fun onReceive(broadcast: CounterBroadcast) {
        when (broadcast) {
            CounterBroadcast.Refresh ->
                event(CounterEvent.ShowMessage("Refreshed!"))
        }
    }
}
```

## 3. Container を作る

`PulseContainer` は ViewModel のリストを受け取り、全員への Broadcast やビューの Refresh を提供します。

```kotlin
class CounterContainer(
    viewModels: List<PulseViewModel<*, *, *, CounterBroadcast, CounterUnicast>>,
) : PulseContainer<CounterBroadcast, CounterUnicast>(viewModels = viewModels)
```

## 4. Compose UI に接続する

### エントリポイント

ViewModel と Container をトップレベルで一度だけ生成します。

```kotlin
fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Counter") {
        MaterialTheme {
            val viewModel = rememberPulseViewModel { CounterViewModel(CounterRepository()) }
            val container = rememberPulseContainer { CounterContainer(viewModels = listOf(viewModel)) }

            CounterScreen(container = container, viewModel = viewModel)
        }
    }
}
```

### Screen の Composable

Screen はルートが表示するもので、feature パッケージのルートに置きます。レイアウトを `PulseHost` で包んで Refresh と Broadcast を使えるようにし、その中に Section を配置します。

```kotlin
@Composable
fun CounterScreen(container: CounterContainer, viewModel: CounterViewModel) {
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

### Section の Composable

Section は 1 つの ViewModel が受け持つ部分で、`section` パッケージに置きます。その ViewModel を `PulseContent` で束ね、State を観測して Event を処理します。

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

## 5. ViewModel を Navigation 3 の destination にスコープする

手順 4 では ViewModel をトップレベルで生成しました。この場合、ViewModel はウィンドウと同じ長さだけ生きます。Navigation 3 の destination の中で生成すると、そのルートがバックスタックにある間だけ生きるようになります。

必要なのは 2 つです。

- `NavDisplay` の `entryDecorators` に `rememberPulseNavEntryDecorators()` を渡す。これでバックスタックの各エントリが独自の `ViewModelStoreOwner` を持ちます
- ViewModel と Container を destination の中で生成する。`NavDisplay` より上では生成しません

この仕組みは [Navigation 3](/ja/guide/navigation3) で解説しています。

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

                            CounterScreen(container = container, viewModel = viewModel)
                        }
                    },
            )
        }
    }
}
```

## デモを動かす

リポジトリにはパルスグリッドのデモが含まれています。4 つのエリアが 1 つの Container を共有しています。1 つをタップすると、辺を共有する 2 つに波及します。デモは app、feature、`core` 配下の domain・data・designsystem に分かれ、Koin で組み立て、カウントを DataStore に保存します。[アーキテクチャ検査](/ja/guide/architecture-checks) が想定する構成で、すべての検査を通ります。リポジトリをクローンして実行してください。

```bash
./gradlew :demo:app:run
```

## 次のステップ

- [アーキテクチャ](/ja/guide/architecture) — データフローを深く理解する
- [ViewModel](/ja/guide/viewmodel) — ViewModel の応用パターン
- [Container](/ja/guide/container) — 複数の ViewModel を調整する
- [Navigation 3](/ja/guide/navigation3) — バックスタックにスコープされたライフタイムの仕組み
