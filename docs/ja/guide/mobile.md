# モバイル

`mvi-mobile` は、Android と iOS の Compose 向けの `mvi` アーティファクトです。スマートフォンは一度に 1 つの画面を表示し、その画面には ViewModel が 1 つあれば足りることがほとんどです。そこで、このアーティファクトは ViewModel 1 つに必要なものだけを持ちます。

## アーティファクトが持つもの

| 残したもの | 外したもの |
|---|---|
| `PulseState`、`PulseAction`、`PulseEvent` | `PulseBroadcast`、`PulseUnicast` |
| `PulseViewModel<State, Action, Event>` | `PulseContainer` |
| `PulseContent` | `PulseHost` |

パッケージは同じ `jp.kaleidot725.pulse.mvi` で、残したものの振る舞いも同じです。

- `onSetup` は `PulseContent` が ViewModel を初めて監視したときに実行されます
- `cancel` と `close` は、そこで始めた処理を終わらせます
- Event は UI が収集するまでバッファされます

`PulseViewModel` の型パラメータは 5 つではなく 3 つで、`onReceive` と `unicast` はありません。

## インストール

このアーティファクトは Kotlin Multiplatform で、Android（`minSdk` 26）、`iosArm64`、`iosSimulatorArm64` のターゲットを持ちます。デスクトップ向けと同じように `settings.gradle.kts` に JitPack を追加します。Compose Multiplatform のプロジェクトでは `commonMain` から `mvi-mobile` に依存すれば、Gradle がターゲットごとに Android 版か iOS 版を選びます。Android だけのアプリでも、同じように `dependencies` に追加します。

```kotlin
// build.gradle.kts
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.github.kaleidot725.PulseDesktop:mvi-mobile:<version>")
        }
    }
}
```

## 画面の例

画面は、アプリで ViewModel を作っている手段で ViewModel を作ります。lifecycle-viewmodel-compose の `viewModel()`（Android でも iOS でも使えます）、`koinViewModel()`、Navigation 3 のエントリの ViewModel デコレータなどです。それを `PulseContent` で結びつけます。戻るなど画面の外に出ていく Event は、画面のコールバックにします。

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

## アーキテクチャ検査

`architecture` アーティファクトは JVM で動くので、Android のユニットテストか JVM のテストモジュールから使います。Konsist で `commonMain` や `iosMain` も含めてソースを読み、クラスのパッケージも同じなので、デスクトップと同じように `PulseViewModel` を見分けます。`PulseContainer`、Broadcast、Unicast の検査は、対象が見つからないだけです。
