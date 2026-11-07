# API 概要

PulsePlatform は役割ごとに 1 つのパッケージを公開しています。ルールは `jp.kaleidot725.pulse.architecture`（`architecture` アーティファクト）にあります。ライブラリは `jp.kaleidot725.pulse.mvi` にあり、デスクトップ向けに `mvi-desktop`、Android と iOS 向けに `mvi-mobile` として公開しています。Navigation 3 対応は `jp.kaleidot725.pulse.navigation` にあり、`navigation-desktop` と `navigation-mobile` として公開しています。

## ルール

テストから呼ぶアサートと、その土台のクエリです。[ルール API](/ja/api/rules) の内容は、検査がどちらかを明記している場合を除き、デスクトップとモバイルのコードの両方に当てはまります。

| アサート | 説明 |
|---|---|
| [`assertPulseMviConventions`](/ja/api/rules#assertpulsemviconventions) | 続く 12 の検査を順に実行する |
| [`assertStatesAreDataTypes`](/ja/api/rules#assertstatesaredatatypes) | `PulseState` は `data class` または `data object` |
| [`assertMessagesAreSealedOrDataTypes`](/ja/api/rules#assertmessagesaresealedordatatypes) | メッセージは `data class` / `data object` のケースを持つ `sealed class`、または単独の `data class` / `data object` |
| [`assertStatesAndMessagesLiveInContracts`](/ja/api/rules#assertstatesandmessagesliveincontracts) | State とメッセージは ViewModel か Container の隣の `…Contracts.kt` に置く |
| [`assertViewModelsAndContainersAreNamedAfterTheirBase`](/ja/api/rules#assertviewmodelsandcontainersarenamedaftertheirbase) | `…ViewModel` と `…Container` |
| [`assertViewModelsHoldTheirDataInTheirState`](/ja/api/rules#assertviewmodelsholdtheirdataintheirstate) | ViewModel と Container は `var` プロパティを持たない |
| [`assertViewModelsLiveInContents`](/ja/api/rules#assertviewmodelsliveincontents) | ViewModel は `content` の下に置く |
| [`assertContainersLiveAtTheFeatureRoot`](/ja/api/rules#assertcontainersliveatthefeatureroot) | Container は feature のルートに置く |
| [`assertHostsComposeContents`](/ja/api/rules#asserthostscomposecontents) | Host は `…Host` という名前で、ViewModel を束ねず Content を並べる |
| [`assertFeaturePackagesHaveARole`](/ja/api/rules#assertfeaturepackageshavearole) | feature の中のファイルは、ルートか `content`・`section`・`component` の下にある |
| [`assertContentsBindAViewModel`](/ja/api/rules#assertcontentsbindaviewmodel) | Content は `PulseContent` で ViewModel を束ねる |
| [`assertSectionsAndComponentsArePure`](/ja/api/rules#assertsectionsandcomponentsarepure) | Section と Component はデータとコールバックだけを受け取る |
| [`assertSectionsAndComponentsHavePreviews`](/ja/api/rules#assertsectionsandcomponentshavepreviews) | すべての Section と Component に `<Name>_Preview` がある |
| [`assertPulseMobileConventions`](/ja/api/rules#assertpulsemobileconventions) | `mvi-mobile` のアプリ向けの検査を順に実行する |
| [`assertMobileFeaturesHaveOneViewModel`](/ja/api/rules#assertmobilefeatureshaveoneviewmodel) | モバイルの ViewModel は feature のルートに 1 つだけ置く |
| [`assertScreensBindTheirViewModel`](/ja/api/rules#assertscreensbindtheirviewmodel) | モバイルの feature のルートの Composable は ViewModel を結びつける `…Screen` |
| [`assertMobileFeaturePackagesHaveARole`](/ja/api/rules#assertmobilefeaturepackageshavearole) | モバイルの feature の中のファイルは、ルートか `section`・`component` の下にある |
| [`assertPulseDomainConventions`](/ja/api/rules#assertpulsedomainconventions) | Repository は Flow を公開し、ワンショットの取得は `Result<V, E>` で返す。UseCase は `usecase` に置き、1 行にしない |
| [`assertPulseModuleConventions`](/ja/api/rules#assertpulsemoduleconventions) | モジュール間の依存が Clean Architecture の向きに従い、domain・data・designsystem がパッケージで分かれている |
| [`assertPulseKoinConventions`](/ja/api/rules#assertpulsekoinconventions) | Koin モジュールは app モジュールで定義する |
| [`assertPulseResourceConventions`](/ja/api/rules#assertpulseresourceconventions) | domain は文言を持たず、Composable は文言を文字列リソースから読む |
| [`PulseModuleLayout`](/ja/api/rules#pulsemodulelayout) | どのモジュール名がどの層に当たるか |

## ライブラリ

`PulseViewModel`、`PulseContent`、次の 3 つのマーカーは `mvi-desktop` と `mvi-mobile` の両方に、Navigation 3 の 2 つの Composable は `navigation-desktop` と `navigation-mobile` の両方にあります。モバイルの `PulseViewModel` は型パラメータを 5 つではなく 3 つ取ります。

| 宣言 | 説明 |
|---|---|
| [`PulseViewModel`](/ja/api/pulse-viewmodel) | 1 つの UI 状態を持ち、Action を処理して Event を発行する抽象基底クラス |
| [`PulseContent`](/ja/api/composables#pulsecontent) | ViewModel を観測し、状態と Action のディスパッチャを渡す |
| [`PulseState`](/ja/api/interfaces#pulsestate) | クラスを ViewModel の UI 状態として印付ける |
| [`PulseAction`](/ja/api/interfaces#pulseaction) | クラスをユーザーの操作として印付ける |
| [`PulseEvent`](/ja/api/interfaces#pulseevent) | クラスを一度きりの副作用として印付ける |
| [`rememberPulseViewModel`](/ja/api/navigation3#rememberpulseviewmodel) | `ViewModelStoreOwner` にスコープされた ViewModel |
| [`rememberPulseNavEntryDecorators`](/ja/api/navigation3#rememberpulsenaventrydecorators) | ViewModel をバックスタックエントリにスコープする `NavDisplay` のデコレータ |

## デスクトップ

`mvi-desktop` と `navigation-desktop` だけにあるものです。

| 宣言 | 説明 |
|---|---|
| [`PulseContainer`](/ja/api/pulse-container) | ウィンドウの ViewModel 群を調整する |
| [`PulseHost`](/ja/api/composables#pulsehost) | Container をサブツリーにスコープし、Refresh と Broadcast のコールバックを渡す |
| [`PulseBroadcast`](/ja/api/interfaces#pulsebroadcast) | クラスを Container の Broadcast メッセージとして印付ける |
| [`PulseUnicast`](/ja/api/interfaces#pulseunicast) | クラスを子から親への Unicast メッセージとして印付ける |
| [`rememberPulseContainer`](/ja/api/navigation3#rememberpulsecontainer) | `ViewModelStoreOwner` にスコープされた Container |
