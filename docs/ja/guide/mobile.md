# モバイルの Screen

`mvi-mobile` は、Android と iOS の Compose Multiplatform 向けの、PulsePlatform の MVI アーティファクトです。スマートフォンは一度に 1 つの画面を表示し、そこには ViewModel が 1 つあれば足ります。そこでモバイルの feature は、ViewModel を 1 つ結びつける **Screen** 1 つになります。複数の ViewModel を調整する Container はなく、Container のためだけにあるものはアーティファクトから外しています。ルールはこの形を [`assertPulseMobileConventions()`](/ja/guide/rules/ui#モバイル) で検査します。

## アーティファクトが持つもの

パッケージはデスクトップと同じ `jp.kaleidot725.pulse.mvi` で、残したものの振る舞いも同じです。`onSetup` は `PulseContent` が ViewModel を初めて監視したときに実行され、`cancel` と `close` はそこで始めた処理を終わらせ、Event は UI が収集するまでバッファされます。`PulseViewModel` の型パラメータは 5 つではなく 3 つで、`onReceive` と `unicast` はありません。

| 残したもの | 外したもの |
|---|---|
| `PulseState`、`PulseAction`、`PulseEvent` | `PulseBroadcast`、`PulseUnicast` |
| `PulseViewModel<State, Action, Event>` | `PulseContainer` |
| `PulseContent` | `PulseHost` |

## Screen

Screen はルートが表示するもので、ViewModel と UI が出会う唯一の場所です。アプリで ViewModel を作っている手段で ViewModel を作ります。Android でも iOS でも使える lifecycle-viewmodel-compose の `viewModel()`、`koinViewModel()`、`navigation-mobile` の `rememberPulseViewModel` などです。それを `PulseContent` で結びつけ、State とコールバックを Section に渡し、戻るなど画面の外に出ていく Event を自分のコールバックに変えます。こうすると、画面遷移は Screen を表示する側に残ります。

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

## ViewModel の寿命

`PulseViewModel` は `androidx.lifecycle.ViewModel` なので、Android でも iOS でも、それを保持する `ViewModelStoreOwner` が寿命を決めます。Screen で呼んだ `viewModel()` は、スコープにあるオーナー（Android では Activity、iOS では Compose が用意するオーナー、Navigation 3 ではバックスタックのエントリ）に ViewModel を保持します。Android の構成変更ではインスタンスが再利用されるので、`onSetup` は再実行されず、State もそのままです。画面を完全に離れるとオーナーがクリアされ、`onCleared` が ViewModel の始めた処理をキャンセルします。

## Navigation 3 と使う

`navigation-mobile` は、Screen の ViewModel を Navigation 3 のバックスタックエントリにスコープします。`NavDisplay` に `rememberPulseNavEntryDecorators()` を渡し、Screen の中で `rememberPulseViewModel`、または同じエントリのオーナーを見つける `viewModel()` で ViewModel を作ります。すると ViewModel は、そのルートがバックスタックにある間だけ生きます。別の Screen に覆われている間は State も処理も保ち、ポップされるとクリアされます。オーナーがどう決まるかは [Navigation 3](/ja/guide/navigation3) にあります。

```kotlin
// build.gradle.kts、commonMain
implementation("com.github.kaleidot725.PulsePlatform:navigation-mobile:<version>")
```

```kotlin
@Composable
fun App() {
    val backStack = remember { mutableStateListOf<Route>(Route.Counter) }
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = rememberPulseNavEntryDecorators(),
        entryProvider =
            entryProvider {
                entry<Route.Counter> {
                    CounterScreen(onBack = { backStack.removeLastOrNull() })
                }
            },
    )
}

@Composable
fun CounterScreen(onBack: () -> Unit) {
    val viewModel = rememberPulseViewModel { CounterViewModel() }
    PulseContent(viewModel = viewModel, onEvent = { onBack() }) { state, onAction ->
        CounterSection(count = state.count, onIncrement = { onAction(CounterAction.Increment) })
    }
}
```

## 次のステップ

- [モバイルのクイックスタート](/ja/guide/getting-started-mobile) — カウンター画面を作って検査する
- [UI の構成のルール](/ja/guide/rules/ui#モバイル) — モバイルの配置をルールごとに
- [Navigation 3](/ja/guide/navigation3) — バックスタックのエントリが ViewModel をどう持つか
- [ViewModel](/ja/guide/viewmodel) — State、Action、Event とライフサイクルフック
