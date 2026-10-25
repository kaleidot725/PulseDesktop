# アーキテクチャ検査

このページの内容はすべて任意のアーティファクト `architecture` にあります。パッケージは `jp.kaleidot725.pulse.architecture` で、いずれも Konsist の `KoScope` の拡張関数です。

それぞれの検査の意味と理由は [アーキテクチャ検査のガイド](/ja/guide/architecture-checks) にあります。

## assertPulseMviConventions

```kotlin
fun KoScope.assertPulseMviConventions()
```

続く 8 つの検査（`assertStatesAreDataTypes` から `assertComponentsHavePreviews` まで）を順に実行します。最初に失敗した検査で例外が投げられるので、1 回の実行で報告されるのは 1 件です。

## assertStatesAreDataTypes

```kotlin
fun KoScope.assertStatesAreDataTypes()
```

`PulseState` を実装するすべての class と object に `data` 修飾子を要求します。

### 失敗時のメッセージ

| 宣言 | メッセージ |
|---|---|
| class | `data` を付けて、等しい State が等しく比較され Compose が再コンポジションをスキップできるようにする |
| object | `data` を付けて、同一性ではなく State として表示・比較されるようにする |

## assertMessagesAreSealedOrDataTypes

```kotlin
fun KoScope.assertMessagesAreSealedOrDataTypes()
```

`PulseAction`、`PulseEvent`、`PulseBroadcast`、`PulseUnicast` を実装する型は `sealed class` に、その中の各ケースは `data class` か `data object` にすることを要求します。ケース自体を `sealed class` にしてさらに分けることもでき、その中のケースにも同じルールが適用されます。ケースが 1 つだけのメッセージは、マーカーを直接実装する 1 つの `data class` か `data object` にしてもかまいません。マーカーを直接実装する interface（sealed interface を含む）と、`data` のない class・object は報告されます。

## assertViewModelsAndContainersAreNamedAfterTheirBase

```kotlin
fun KoScope.assertViewModelsAndContainersAreNamedAfterTheirBase()
```

`PulseViewModel` のサブクラスに `ViewModel` で終わる名前を、`PulseContainer` のサブクラスに `Container` で終わる名前を要求します。

## assertViewModelsHoldTheirDataInTheirState

```kotlin
fun KoScope.assertViewModelsHoldTheirDataInTheirState()
```

`PulseViewModel` と `PulseContainer` のサブクラスが `var` プロパティを宣言していないことを要求します。

## assertSectionsBindOneViewModel

```kotlin
fun KoScope.assertSectionsBindOneViewModel()
```

`section` パッケージ配下の Composable を持つすべてのファイルに `PulseContent` の呼び出しを要求します。判定はファイル単位なので、Section の隣に置かれる stateless なオーバーロードや `@Preview` は問題ありません。

## assertComponentsAreStateless

```kotlin
fun KoScope.assertComponentsAreStateless()
```

`component` パッケージ配下のすべての Composable に、型名が `ViewModel` または `Container` で終わる引数がないこと、そしてそのファイルが `PulseContent` も `PulseHost` も呼ばないことを要求します。

## assertScreensComposeSections

```kotlin
fun KoScope.assertScreensComposeSections()
```

`section` にも `component` にも属さない Composable を持つファイル、つまり Screen に、`PulseContent` を呼ばないことを要求します。

## assertComponentsHavePreviews

```kotlin
fun KoScope.assertComponentsHavePreviews()
```

`component` パッケージ配下の public な Composable それぞれに、`<Component>_Preview` または `<Component>_Preview_<条件>` という名前で `@Preview`（パッケージは問わず、名前が `Preview` で始まるマルチプレビュー注釈も含む）が付いた関数があることを要求します。Preview はスコープ全体から探します。private な Composable と Preview 自身は対象外です。

## assertPulseDomainConventions

```kotlin
fun KoScope.assertPulseDomainConventions()
```

以下の 2 つの Repository の検査と 2 つの UseCase の検査を実行します。Repository は名前が `Repository` で終わる class または interface です。class と interface をまとめて検査するので、失敗時は違反したものをすべて一度に挙げます。UseCase は名前が `UseCase` で終わる class です。

## assertRepositoriesExposeFlowsOrStateFlows

```kotlin
fun KoScope.assertRepositoriesExposeFlowsOrStateFlows()
```

Repository の public プロパティの型が `Flow` または `StateFlow` であることを要求します。Repository 型や関数型のプロパティは対象外で、型を書いていないプロパティは違反になります。

## assertRepositoriesReturnResultsFromOneShotCalls

```kotlin
fun KoScope.assertRepositoriesReturnResultsFromOneShotCalls()
```

値を返す Repository の public 関数が、`Flow`、`StateFlow`、または `com.github.michaelbull.result.Result`（import か完全修飾名で判定）を返すことを要求します。`Unit` を返す関数と、型を書いていないブロック本体の関数はコマンドとして扱い、検査しません。型を書いていない式本体の関数は違反になります。

## assertUseCasesLiveInTheUseCasePackage

```kotlin
fun KoScope.assertUseCasesLiveInTheUseCasePackage()
```

UseCase が、`usecase` という部分を含むパッケージにあることを要求します。

## assertUseCasesDoMoreThanOneLine

```kotlin
fun KoScope.assertUseCasesDoMoreThanOneLine()
```

UseCase の public 関数の本体が、空行を除いて 2 行以上であることを要求します。本体は、式本体なら `=` のあと、ブロック本体なら波括弧の中です。private 関数は検査しません。

## assertPulseModuleConventions

```kotlin
fun KoScope.assertPulseModuleConventions(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid)
```

以下のモジュールの検査を、同じレイアウトですべて実行します。依存は、各パッケージを宣言しているモジュールに対応付けたうえで、ファイルが import しているモジュールとして求めます。どの層にも当てはまらないモジュールのファイルは検査しません。

| アサート | 要求すること |
|---|---|
| `assertNothingDependsOnTheApp(layout)` | どのモジュールも app から import しない |
| `assertFeaturesDoNotDependOnEachOther(layout)` | feature はほかの feature から import しない |
| `assertCoreDoesNotDependOnFeatures(layout)` | app と feature 以外のモジュールは feature から import しない |
| `assertDomainDependsOnNothing(layout)` | domain は補助モジュールからだけ import する |
| `assertDataDependsOnlyOnTheDomain(layout)` | data は domain と補助モジュールからだけ import する |
| `assertDesignSystemDependsOnNothing(layout)` | designsystem は補助モジュールからだけ import する |
| `assertSharedUiDependsOnlyOnTheDomain(layout)` | 共有 UI は domain、designsystem、自身、補助モジュールからだけ import する |
| `assertFeaturesReachDataThroughTheDomain(layout)` | feature は data から何も import しない |
| `assertDomainIsFrameworkFree(layout)` | domain は `androidx.compose`、`androidx.lifecycle`、`jp.kaleidot725.pulse.mvi`、`jp.kaleidot725.pulse.navigation`、`jp.kaleidot725.pulse.architecture` から import しない |
| `assertDomainIsSplitIntoItsPackages(layout)` | domain のファイルは `model`・`repository`・`usecase` のどれかを含むパッケージにある |
| `assertDataIsSplitIntoItsPackages(layout)` | data のファイルは `repository`・`local`・`model` のどれかを含むパッケージにある |

## PulseModuleLayout

```kotlin
data class PulseModuleLayout(
    val app: List<String> = listOf("app"),
    val features: List<String> = listOf("feature/*", "feature/*/api", "feature/*/impl"),
    val domain: List<String> = listOf("core/domain"),
    val data: List<String> = listOf("core/data"),
    val sharedUi: List<String> = listOf("core/ui"),
    val support: List<String> = listOf("core/*"),
    val designSystem: List<String> = listOf("core/designsystem"),
) {
    companion object {
        val NowInAndroid: PulseModuleLayout
    }
}
```

モジュールのディレクトリを層に対応付けます。各パターンはモジュールのディレクトリ（`src` の 1 つ上）の末尾と照合し、`*` はちょうど 1 階層を表します。層は宣言順に判定し、最初に当てはまった層に属します。末尾の `api` や `impl` は、その上の feature の一部として扱います。デフォルトは `NowInAndroid` です。

## pulseModule

```kotlin
fun KoFileDeclaration.pulseModule(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid): String?
```

ファイルが属するモジュールの名前を、ディレクトリのうちパターンに一致した部分（`core/data` や `feature/counter`）として返します。どの層にも当てはまらない場合は null を返します。

## assertPulseKoinConventions

```kotlin
fun KoScope.assertPulseKoinConventions(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid)
```

以下の Koin の検査を実行します。Koin を使っていないプロジェクトは通ります。

## assertKoinModulesLiveInTheApp

```kotlin
fun KoScope.assertKoinModulesLiveInTheApp(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid)
```

`org.koin.dsl.module` か `org.koin.core.annotation.Module` を import するファイルが、レイアウトの app 層にあることを要求します。どの層にも当てはまらないモジュールのファイルは検査しません。

## クエリ

クエリは上のアサートの土台であり、プロジェクト独自のルールの土台にもなります。型のクエリは対応する型を直接継承・実装している宣言を返し、View のクエリは役割が関数ではなくファイルのパッケージで決まるため、ファイルを返します。

| クエリ | 戻り値 |
|---|---|
| `pulseViewModels()` | `List<KoClassDeclaration>` — `PulseViewModel` を継承した class |
| `pulseContainers()` | `List<KoClassDeclaration>` — `PulseContainer` を継承した class |
| `pulseStateClasses()` | `List<KoClassDeclaration>` — `PulseState` を実装した class |
| `pulseStateObjects()` | `List<KoObjectDeclaration>` — `PulseState` を実装した object |
| `pulseMessageInterfaces()` | `List<KoInterfaceDeclaration>` — 4 つのメッセージマーカーのいずれかを実装した interface |
| `pulseMessageClasses()` | `List<KoClassDeclaration>` — 同じマーカーを実装した class |
| `pulseMessageObjects()` | `List<KoObjectDeclaration>` — 同じマーカーを実装した object |
| `pulseScreens()` | `List<KoFileDeclaration>` — `section` と `component` のどちらにも属さない Composable を持つファイル |
| `pulseSections()` | `List<KoFileDeclaration>` — `section` 配下の Composable を持つファイル。内側の `component` を除く |
| `pulseComponents()` | `List<KoFileDeclaration>` — `component` 配下の Composable を持つファイル |
| `pulseRepositoryClasses()` | `List<KoClassDeclaration>` — 名前が `Repository` で終わる class |
| `pulseRepositoryInterfaces()` | `List<KoInterfaceDeclaration>` — 名前が `Repository` で終わる interface |
| `pulseRepositories()` | `List<KoBaseDeclaration>` — 上の 2 つを合わせたもの |
| `pulseUseCases()` | `List<KoClassDeclaration>` — 名前が `UseCase` で終わる class |
| `pulseKoinModuleFiles()` | `List<KoFileDeclaration>` — Koin モジュールを定義するファイル |

## スコープ

アサートは任意の `KoScope` に対して動くので、何を検査するかはスコープの選び方で決まります。

| スコープ | 読む範囲 |
|---|---|
| `Konsist.scopeFromProduction()` | プロジェクトのプロダクションのソースセット。テストコードを除く |
| `Konsist.scopeFromProject()` | すべてのソースセット。テストコードを含む |
| `Konsist.scopeFromProject(moduleName = "app")` | 単一のモジュール |
| `Konsist.scopeFromPackage("com.example.counter..")` | 単一のパッケージとその配下 |

## 次のステップ

- [アーキテクチャ検査のガイド](/ja/guide/architecture-checks) — セットアップと各検査の理由
- [マーカーインターフェース](/ja/api/interfaces) — 検査の対象になっている型
