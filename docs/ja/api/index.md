# API 概要

PulseDesktop の API は小さく、焦点が絞られています。アーティファクトとパッケージは 1 対 1 です。コアの `mvi` は `jp.kaleidot725.pulse.mvi`、任意の `navigation` と `architecture` はそれぞれ `jp.kaleidot725.pulse.navigation` と `jp.kaleidot725.pulse.architecture` にあります。後者 2 つは [拡張](#拡張) にまとめています。

## クラス

| クラス | 説明 |
|---|---|
| [`PulseViewModel`](/ja/api/pulse-viewmodel) | UI 状態を管理する抽象基底クラス |
| [`PulseContainer`](/ja/api/pulse-container) | 複数の ViewModel を調整する |

## Composable

| Composable | 説明 |
|---|---|
| [`PulseHost`](/ja/api/composables#pulsehost) | Container をサブツリーにスコープし、Refresh と Broadcast のコールバックを提供する |
| [`PulseContent`](/ja/api/composables#pulsecontent) | ViewModel を観測し、状態と Action のディスパッチャを提供する |

## マーカーインターフェース

| インターフェース | 説明 |
|---|---|
| [`PulseState`](/ja/api/interfaces#pulsestate) | クラスを ViewModel の UI 状態として印付ける |
| [`PulseAction`](/ja/api/interfaces#pulseaction) | クラスをユーザーの Action として印付ける |
| [`PulseEvent`](/ja/api/interfaces#pulseevent) | クラスを一度きりの副作用として印付ける |
| [`PulseBroadcast`](/ja/api/interfaces#pulsebroadcast) | クラスを Container の Broadcast メッセージとして印付ける |
| [`PulseUnicast`](/ja/api/interfaces#pulseunicast) | クラスを子から親への Unicast メッセージとして印付ける |

## 拡張

### Navigation 3

| Composable | 説明 |
|---|---|
| [`rememberPulseViewModel`](/ja/api/navigation3#rememberpulseviewmodel) | `ViewModelStoreOwner` にスコープされた ViewModel |
| [`rememberPulseContainer`](/ja/api/navigation3#rememberpulsecontainer) | `ViewModelStoreOwner` にスコープされた Container |
| [`rememberPulseNavEntryDecorators`](/ja/api/navigation3#rememberpulsenaventrydecorators) | ViewModel をバックスタックエントリにスコープする `NavDisplay` のデコレータ |

### アーキテクチャ検査

| アサート | 説明 |
|---|---|
| [`assertPulseMviConventions`](/ja/api/architecture#assertpulsemviconventions) | 以下のすべての検査を順に実行する |
| [`assertStatesAreDataTypes`](/ja/api/architecture#assertstatesaredatatypes) | `PulseState` は `data class` または `data object` |
| [`assertMessagesAreSealedOrDataTypes`](/ja/api/architecture#assertmessagesaresealedordatatypes) | メッセージの interface は `sealed`、直接実装する class・object は `data` |
| [`assertViewModelsAndContainersAreNamedAfterTheirBase`](/ja/api/architecture#assertviewmodelsandcontainersarenamedaftertheirbase) | `…ViewModel` と `…Container` |
| [`assertViewModelsHoldTheirDataInTheirState`](/ja/api/architecture#assertviewmodelsholdtheirdataintheirstate) | ViewModel と Container は `var` プロパティを持たない |
| [`assertSectionsBindOneViewModel`](/ja/api/architecture#assertsectionsbindoneviewmodel) | Section は `PulseContent` で ViewModel を束ねる |
| [`assertComponentsAreStateless`](/ja/api/architecture#assertcomponentsarestateless) | Component はデータとコールバックだけを受け取る |
| [`assertScreensComposeSections`](/ja/api/architecture#assertscreenscomposesections) | Screen は ViewModel を束ねず Section を並べる |
