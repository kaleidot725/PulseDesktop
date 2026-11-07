# デスクトップのクイックスタート

このガイドでは、Compose Desktop でカウンターのウィンドウを作ります。Host、Content 1 つ、Container からなり、最後にルールで検査します。Android と iOS については [モバイルのクイックスタート](/ja/guide/getting-started-mobile) を参照してください。

ViewModel の生成には `navigation-desktop` が追加する `rememberPulseViewModel` を使うので、`mvi-desktop` と一緒に追加し、ルールはテストに追加してください。`mvi-desktop` だけでライフサイクルを自前で扱う場合は [ViewModel](/ja/guide/viewmodel) を参照してください。

```kotlin
dependencies {
    implementation("com.github.kaleidot725.PulsePlatform:mvi-desktop:<version>")
    implementation("com.github.kaleidot725.PulsePlatform:navigation-desktop:<version>")
    testImplementation("com.github.kaleidot725.PulsePlatform:architecture:<version>")
}
```

## 1. State・Action・Event・Broadcast・Unicast を定義する

まず、機能を表す 5 つの型を定義します。これらは使うクラスの隣の Contracts ファイルに置きます。State・Action・Event は ViewModel の隣の `content/counter/CounterContentContracts.kt` に、Broadcast・Unicast は Container の隣の `CounterContracts.kt` に置きます。ここではまとめて示します。

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
sealed class CounterUnicast : PulseUnicast {
    data object ResetRequested : CounterUnicast()
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

            CounterHost(container = container, viewModel = viewModel)
        }
    }
}
```

### Host の Composable

Host はルートが表示するもので、feature パッケージのルートに `CounterHost.kt` として、`CounterContainer` の隣に置きます。レイアウトを `PulseHost` で包んで Refresh と Broadcast を使えるようにし、その中に Content を配置します。

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

### Content の Composable

Content は 1 つの ViewModel が受け持つ部分で、`content/counter` に `CounterViewModel` と並べて置きます。その ViewModel を `PulseContent` で束ね、State を観測して Event を処理します。

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

                            CounterHost(container = container, viewModel = viewModel)
                        }
                    },
            )
        }
    }
}
```

## 6. ルールで検査する

1 本のテストで、プロジェクト全体にルールを実行します。手順 1 と手順 4 のとおりにファイルを置いていれば通ります。Host と Container は feature のルートに、Content とその ViewModel は `content/counter` に置きます。`./gradlew test` で実行し、失敗するとファイルの名前と直し方が示されます。このカウンターは短く保つためにラベルをリテラルで書いているので、UI の構成のルールだけを実行します。実際のアプリでは、ラベルを文字列リソースへ移す文言のルールなど、[ほかのグループ](/ja/guide/rules/) も加えます。

```kotlin
class PulsePlatformConventionTest {
    @Test
    fun `follows the PulsePlatform conventions`() {
        val scope = Konsist.scopeFromProduction()
        scope.assertPulseMviConventions()
    }
}
```

## デモを動かす

リポジトリにはパルスグリッドのデモが含まれています。4 つのエリアが 1 つの Container を共有しています。1 つをタップすると、辺を共有する 2 つに波及します。デモは app、feature、`core` 配下の domain・data・designsystem に分かれ、Koin で組み立て、カウントを DataStore に保存します。[ルール](/ja/guide/rules/) が想定する構成で、すべてのルールを通ります。リポジトリをクローンして実行してください。

```bash
./gradlew :demo:app:run
```

## 次のステップ

- [UI の構成のルール](/ja/guide/rules/ui) — テストが検査するすべてのルール
- [MVI](/ja/guide/mvi) — データフローを深く理解する
- [ViewModel](/ja/guide/viewmodel) — ViewModel の応用パターン
- [Host と Container](/ja/guide/container) — 複数の ViewModel を調整する
- [Navigation 3](/ja/guide/navigation3) — バックスタックにスコープされたライフタイムの仕組み
