# UI の構成

これらのルールは、feature をどう分け、それぞれの部分が何を持ってよいかを決めます。PulsePlatform の中心です。ライブラリには役割ごとに部品が 1 つずつあり、ルールはどの部品も決まった場所にあるかを検査します。デスクトップのアプリは `assertPulseMviConventions()`、モバイルのアプリは `assertPulseMobileConventions()` で実行します。State、メッセージ、Section、Component、Preview のルールはどちらも同じで、違うのは feature の配置だけです。失敗すると、宣言やファイルの名前と直し方が示されます。

## State とメッセージ

これらのルールは、デスクトップでもモバイルでも同じです。

| 検査 | ルール | 理由 |
|---|---|---|
| State は data 型 | `PulseState` は `data class` または `data object` | `StateFlow` は直前と等しい値を流さず、Compose は引数が等しい Composable をスキップする。`equals` がないと更新ごとに画面全体が再コンポジションされる |
| メッセージは sealed class | Action・Event・Broadcast・Unicast は `sealed class`、その中の各ケースは `data class` か `data object`。ケースが 1 つなら単独の `data class` か `data object` でもよく、interface は使わない | どれも `when` で処理される。sealed にしてあればケースの追加がコンパイルエラーになり、`data` にしてあればケースが値で比較・表示される |
| State とメッセージは Contracts に置く | `PulseState`・Action・Event・Broadcast・Unicast は、ViewModel か Container と同じパッケージの `…Contracts.kt` にある | ViewModel や Container が取り決める型なので、使うクラスの隣の 1 ファイルで見つかるようにする。`PulseAreaViewModel` なら `PulseAreaContracts.kt`、`PulseCountContainer` なら `PulseCountContracts.kt` |
| 名前は基底クラスに従う | `PulseViewModel` は `…ViewModel`、`PulseContainer` は `…Container` | コンパイラにとってはどちらも `ViewModel` のサブクラスなので、どちらなのかを示すのは名前だけ |
| データは State に置く | ViewModel と Container は `var` プロパティを持たない | State の外の `var` は変更しても emission が起きず、何も再コンポジションされない |

## デスクトップ

feature は 4 つの役割に分けます。**Host** はルートが表示するもので、ViewModel と Container を生成し、Content を配置します。**Content** は 1 つの ViewModel が受け持つ部分で、その ViewModel を `PulseContent` で束ねます。**Section** は State とコールバックから画面の一部を描き、**Component** は小さな部品です。Section と Component はどちらも純粋な Composable で、違うのは役割だけです。

どれに当たるかはパッケージで決まるので、Host 以外の名前は自由です。feature のルートは `content` パッケージを持つパッケージで、そこに Host（`…Host` という名前のファイル）、Container、Container の Contracts を置きます。Content は `content` の下にそれぞれのパッケージを持ち、ViewModel と Contracts をその隣に置きます。Section と Component は、feature のルートにも、それを使う Content の中にも置けます。ルートの下に置けるパッケージは、この 3 つだけです。`stringResource` でラベルを読むような、feature 全体で使う純粋な関数は `component` に置きます。値を返すだけの Composable は View ではないので、Preview はいりません。

```
count/
├── PulseCountHost.kt          # Host
├── PulseCountContainer.kt
├── PulseCountContracts.kt     # Container の Broadcast と Unicast
├── component/                 # feature 全体で使う Component
│   └── PulseCountHeader.kt
└── content/
    └── area/
        ├── PulseAreaContent.kt    # Content
        ├── PulseAreaViewModel.kt
        ├── PulseAreaContracts.kt  # ViewModel の State・Action・Event
        ├── section/               # この Content の Section
        │   └── PulseAreaSection.kt
        └── component/             # この Content の Component
            └── PulseAreaCell.kt
```

| 検査 | ルール | 理由 |
|---|---|---|
| ViewModel は Content に置く | `PulseViewModel` は `content` の下で、`section` と `component` の外にある | ViewModel は 1 つの Content を受け持つので、それを束ねる Composable の隣に置き、下の純粋な部分からは見えないようにする |
| Container は feature のルートに置く | `PulseContainer` は `content` を持つパッケージにある | Container は feature のすべての Content をまとめるので、それを生成する Host の隣に置く |
| Host は Content を並べる | feature のルートにある Composable は `…Host` という名前のファイルにあり、`PulseContent` を呼ばない | Content ではなく Host で束ねると、Host がすべてを知るファイルに育つ |
| feature のパッケージは決まっている | feature の中のファイルは、ルートか、`content`・`section`・`component` のどれかの下にある。`build/generated` の下の生成コードは対象外 | `text` のような独自のパッケージは、誰も決めていない置き場所になる |
| Content は ViewModel を束ねる | `content` の下で `section` と `component` の外にある Composable を持つファイルは `PulseContent` を呼ぶ | ViewModel と UI が出会うのは Content だけ。何も束ねていないファイルは、置き場所を間違えた Section |
| Section と Component は純粋 | `section` と `component` の下の Composable は `…ViewModel` や `…Container` を引数に取らず、`PulseContent` も `PulseHost` も呼ばない | データとコールバックだけで済むから、単体で Preview でき、使い回せる |
| Section と Component には Preview がある | `section` と `component` の下の public な Composable には、対応する `@Preview` がある | 純粋な Composable は Preview があれば単体で確認でき、壊れたときに最初に気づける |

## モバイル

[`mvi-mobile`](/ja/guide/mobile) で作ったアプリでは、`assertPulseMviConventions()` の代わりに `assertPulseMobileConventions()` を呼びます。スマートフォンは一度に 1 つの画面を表示し、`mvi-mobile` には Container がないので、feature は **Screen** 1 つと ViewModel 1 つです。Host も `content` パッケージもありません。feature のルートは ViewModel があるパッケージです。Screen も同じパッケージの `…Screen` という名前のファイルに置き、`PulseContent` で ViewModel を結びつけます。Section と Component はデスクトップと同じで、ルートの下に置けるパッケージは `section` と `component` だけです。 表の配置の検査がデスクトップのものに替わり、このページのほかのルールはそのまま当てはまります。

```
counter/
├── CounterScreen.kt       # Screen: CounterViewModel を PulseContent で結びつける
├── CounterViewModel.kt
├── CounterContracts.kt    # ViewModel の State、Action、Event
├── section/
│   └── CounterSection.kt
└── component/
    └── CounterBadge.kt
```

| 検査 | ルール | 理由 |
|---|---|---|
| feature の ViewModel は 1 つ | `PulseViewModel` は `section`・`component`・`content` の外にあり、同じパッケージにほかの ViewModel がない | 1 つの画面には ViewModel が 1 つ。2 つ目は、自分の feature を持たない 2 つ目の画面 |
| Screen が ViewModel を結びつける | feature のルートにある Composable は `…Screen` という名前のファイルにあって `PulseContent` を呼び、どの ViewModel の隣にもそういう Screen がある | Screen はルートが表示するもので、ViewModel と UI が出会う唯一の場所 |
| feature には決まったパッケージだけ | feature の中のファイルは、ルートか `section`・`component` の下にある。ビルドが生成したコードは対象外 | 独自のパッケージは、誰も決めていない置き場所が 1 つ増えるだけ |

## Preview

Preview は名前で Section や Component と対応付けます。`<Name>_Preview`、または特定の状態を示す `<Name>_Preview_<条件>` です。1 つあれば十分で、状態がいくつもあるものは条件ごとに置けます。それ以外の名前の Preview は数えないので、Composable だけリネームして Preview が古い名前のまま残った場合も検出できます。

| Preview | `PulseTextButton` に対応するか |
|---|---|
| `PulseTextButton_Preview` | する |
| `PulseTextButton_Preview_Disabled` | する |
| `PulseTextButton_Preview_Disabled_Dark` | する |
| `PulseTextButtonPreview` | しない |

## 次のステップ

- [ドメインとデータ](/ja/guide/rules/domain) — Repository と UseCase
- [ルール API](/ja/api/rules) — すべてのアサートとクエリのシグネチャ
