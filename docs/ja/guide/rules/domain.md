# ドメインとデータ

これらのルールは UI の下の層を予測しやすく保ち、ViewModel がいつも同じ形でデータを受け取れるようにします。`assertPulseDomainConventions()` で実行し、デスクトップでもモバイルでも同じです。

## Repository

`assertPulseDomainConventions()` は、Repository がデータをどう公開するかを検査します。Repository は、名前が `Repository` で終わる class または interface です。

| 検査 | ルール | 理由 |
|---|---|---|
| 購読する値は Flow で公開する | public プロパティはコールド `Flow` または `StateFlow` | 読む側はその値を購読し続ける。`SharedFlow` は購読前の値を落とし、public な `MutableStateFlow` は呼び出し側に書き換えさせ、ただの値は読んだ時点で古くなる |
| ワンショットの取得は Result で返す | 値を返す public 関数は `Flow`、`StateFlow`、または kotlin-result の `Result<V, E>` を返す | ワンショットの呼び出しは失敗しうる。`Result<V, E>` なら失敗とその種類が型に現れる。エラーの型を持たない `kotlin.Result` は対象外 |

### Repository に許されていること

次のものは許可されていて、Repository が公開するデータとしては数えません。

- 値を返さない関数はコマンドとして扱い、検査しない
- 別の Repository や、Data モジュールが公開する関数などを保持するプロパティは、Repository が使うもの
- public な `StateFlow` の裏にある private な `MutableStateFlow` は、Repository が自分の状態を変えるためのもの

## UseCase

ViewModel は Repository を直接呼んでかまわないので、UseCase は必須ではありません。2 つの Flow を組み合わせる、動くかどうかを判断するなど、UseCase 自身の仕事があるときだけ作ります。`assertPulseDomainConventions()` は、作った UseCase を検査します。UseCase は、名前が `UseCase` で終わる class です。

| 検査 | ルール | 理由 |
|---|---|---|
| UseCase はパッケージを分ける | UseCase は `usecase` パッケージに置く | エンティティ・Repository の interface・UseCase は読む目的が違うので、それぞれに置き場所を持たせる |
| UseCase は自分の仕事をする | UseCase の public 関数は 1 行にしない | 1 行の UseCase は Repository に呼び出しを渡すだけで、ViewModel が自分で呼べる処理のために、読むクラスと組み立てるクラスが 1 つ増える |

## 次のステップ

- [モジュールと Koin](/ja/guide/rules/modules) — ドメインとデータの層をどこに置くか
