# モバイルのクイックスタート

このガイドでは、`mvi-mobile` で Android と iOS のカウンター画面を作り、ルールで検査します。Android と iOS のターゲットを持つ Compose Multiplatform のプロジェクトを前提にしています。

## 1. アーティファクトを追加する

`mvi-mobile` は `commonMain` に追加し、Gradle がターゲットごとに Android 版か iOS 版を選びます。ルールは JVM で動くので、Android のユニットテストに追加します。`lifecycle-viewmodel-compose` があれば、どちらのプラットフォームでも `viewModel()` を使えます。

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

## 2. feature の配置を決める

モバイルの feature は、Screen 1 つと ViewModel 1 つを 1 つのパッケージに置き、その下に `section` と `component` を置いたものです。ルールはこの配置を検査するので、最初に作っておく価値があります。

```
counter/
├── CounterScreen.kt        # CounterViewModel を PulseContent で結びつける
├── CounterViewModel.kt
├── CounterContracts.kt     # ViewModel の State、Action、Event
└── section/
    └── CounterSection.kt   # State を描き、コールバックを受け取る
```

## 3. Contracts を定義する

State は 1 つの `data class` です。Action と Event は `data` のケースを持つ sealed class です。3 つとも ViewModel の隣の `CounterContracts.kt` に置きます。

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

## 4. ViewModel を書く

モバイルの `PulseViewModel` は型パラメータを 3 つ取ります。`onAction` でユーザーの操作を処理し、`update` で次の State を作り、`event` で戻るなどの一度きりの副作用を送ります。`onSetup` は Screen が ViewModel を初めて監視したときに 1 回だけ実行され、Repository の Flow を収集するならここに書きます。

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

## 5. Section と Screen を書く

Section は State を描き、コールバックだけを受け取るので、単体の Preview を持てます。ルールが求めるとおり、文言は文字列リソースから読みます。Screen は ViewModel を作って `PulseContent` で結びつけ、State を Section に渡し、画面の外に出ていく Event を自分のコールバックに変えます。

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

## 6. ルールで検査する

Android のユニットテストに 1 本テストを書くと、`commonMain` と `iosMain` も含めてプロジェクト全体にルールを実行します。`./gradlew testDebugUnitTest` で実行します。失敗すると、ファイルの名前と直し方が示されます。

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

## 次のステップ

- [モバイルの Screen](/ja/guide/mobile) — `mvi-mobile` が持つものと、Screen の ViewModel の寿命
- [UI の構成のルール](/ja/guide/rules/ui#モバイル) — モバイルの配置をルールごとに
- [AI エージェントと作る](/ja/guide/ai-agents) — 同じテストをエージェントに実行させる
