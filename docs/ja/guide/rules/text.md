# 文言

`assertPulseResourceConventions()` は、アプリを翻訳できる状態に保ちます。描画するモジュール（feature、`core/ui`、`core/designsystem`）は、それぞれ自分の文言を Compose Multiplatform の `composeResources` に文字列リソースとして持ち、`stringResource(Res.string.…)` で読みます。翻訳は `values` の隣に置く `values-<言語>` ディレクトリです。Android だけのアプリなら、Android のリソースと `stringResource(R.string.…)` を使ってもかまいません。検査が見るのはリテラルで、文言をどこから持ってくるかではないからです。モジュールの検査と同じ `PulseModuleLayout` を使い、デスクトップでもモバイルでも同じように当てはまります。

ラベルが必要なエンティティには、描画するモジュールでラベルを付けます。検査を表に、デモがエリアの位置にラベルを付ける例をその下に示します。

| 検査 | ルール | 理由 |
|---|---|---|
| domain は文言を持たない | domain の文字列リテラルに文字を含めない | domain に置いたラベルは、それを表示するすべての画面で 1 つの言語になる。domain はケースを型で表し、描画するモジュールが文字列リソースに対応付ける |
| Composable は文言をリソースから読む | Composable は `Text("…")` や、`text`・`title`・`label`・`placeholder`・`contentDescription` の引数にリテラルを渡さない | そこに書いたリテラルは 1 つの言語でしか読めない。`"$count"` のように値を埋め込むだけのリテラルと、`@Preview` の中は対象外 |

```kotlin
// feature/count — domain の enum は自分のラベルを持たない
internal val PulseAreaPosition.label: StringResource
    get() =
        when (this) {
            PulseAreaPosition.TopLeft -> Res.string.position_top_left
            PulseAreaPosition.TopRight -> Res.string.position_top_right
            PulseAreaPosition.BottomLeft -> Res.string.position_bottom_left
            PulseAreaPosition.BottomRight -> Res.string.position_bottom_right
        }
```

## 次のステップ

- [ルール](/ja/guide/rules/) — テストを用意し、ルールを選ぶ
