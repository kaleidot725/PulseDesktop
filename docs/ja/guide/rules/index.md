# ルール

ルールは、PulsePlatform がそれを使うコードに求めることを [Konsist](https://docs.konsist.lemonappdev.com/) のアサートとして書いたもので、自分のテストスイートから実行します。`architecture` アーティファクトとして公開していて、JVM で動きます。そのため、デスクトップのアプリも、Android と iOS のアプリの Kotlin も同じように検査できます。Pulse の型は完全修飾名で見分け、`mvi-desktop` にも `mvi-mobile` にも依存しないので、モバイルアプリのテストに追加してもデスクトップのクラスがクラスパスに入ることはありません。

型で表せることは、すでにコンパイラが弾きます。ViewModel は `onAction` を実装しなければならず、メッセージの型は Container が知っているものでなければなりません。ルールが扱うのは型では表せない習慣で、どれもレビューで見落としやすく、AI エージェントが外れやすいものです。失敗するとファイルや宣言の名前と直し方が示されるので、人でもエージェントでも、このサイトを開かずに対応できます。

ルールはグループに分かれていて、グループごとにアサートが 1 つあります。どのグループも単独で使えます。

| アサート | 検査する内容 | ページ |
|---|---|---|
| `assertPulseMviConventions()` | デスクトップの feature を Host・Content・Section・Component に分けること、State とメッセージ、Preview | [UI の構成](/ja/guide/rules/ui) |
| `assertPulseMobileConventions()` | モバイルの feature を Screen 1 つと ViewModel 1 つにすること、State とメッセージ、Preview | [UI の構成](/ja/guide/rules/ui#モバイル) |
| `assertPulseDomainConventions()` | Repository がデータをどう公開するか、UseCase を作ってよいのはどんなときか | [ドメインとデータ](/ja/guide/rules/domain) |
| `assertPulseModuleConventions()` | どのモジュールがどのモジュールに依存してよいか、domain・data・デザインシステムの中のパッケージ | [モジュールと Koin](/ja/guide/rules/modules) |
| `assertPulseKoinConventions()` | Koin のモジュールをどこで定義するか | [モジュールと Koin](/ja/guide/rules/modules#koin) |
| `assertPulseResourceConventions()` | ユーザーが読む文言をどこから持ってくるか | [文言](/ja/guide/rules/text) |

## セットアップ

テストの依存に追加し、プロジェクトで使うグループを実行するテストを 1 本書きます。Konsist 自体も一緒に入るので、ほかに宣言するものはありません。`scopeFromProduction()` は `commonMain` や `iosMain` も含むプロダクションのソースセットを読み、テストコードを除きます。

```kotlin
// build.gradle.kts
dependencies {
    testImplementation("com.github.kaleidot725.PulsePlatform:architecture:<version>")
}
```

```kotlin
class PulsePlatformConventionTest {
    @Test
    fun `follows the PulsePlatform conventions`() {
        val scope = Konsist.scopeFromProduction()
        scope.assertPulseMviConventions() // モバイルでは assertPulseMobileConventions()
        scope.assertPulseDomainConventions()
        scope.assertPulseModuleConventions()
        scope.assertPulseKoinConventions()
        scope.assertPulseResourceConventions()
    }
}
```

## ルールを選ぶ

それぞれの検査は単体でも公開しています。合わない検査があるプロジェクトは、ほかの検査だけを実行できます。

```kotlin
@Test
fun `follows the conventions we agreed on`() {
    val scope = Konsist.scopeFromProduction()
    scope.assertStatesAreDataTypes()
    scope.assertMessagesAreSealedOrDataTypes()
    scope.assertViewModelsHoldTheirDataInTheirState()
}
```

## 自分のルールを書く

検査の土台になっているクエリも公開しているので、その上に独自のルールを足せます。`pulseViewModels()`、`pulseContainers()`、`pulseStateClasses()`、`pulseStateObjects()`、`pulseMessageInterfaces()`、`pulseMessageClasses()`、`pulseMessageObjects()` は、対応する型を継承・実装している宣言を返します。`pulseFeatureRoots()` は各 feature のルートのパッケージを返し、`pulseHosts()`、`pulseContents()`、`pulseSections()`、`pulseComponents()` は各役割の Composable を持つファイルを返します。モバイルでは、`pulseMobileFeatureRoots()` と `pulseScreens()` が Screen について同じことをします。`pulseRepositoryClasses()`、`pulseRepositoryInterfaces()`、`pulseRepositories()` は Repository を、`pulseKoinModuleFiles()` は Koin モジュールを定義するファイルを返し、`pulseModule(layout)` はファイルが属するモジュールの名前を返します。

```kotlin
@Test
fun `every ViewModel lives in a feature package`() {
    Konsist
        .scopeFromProduction()
        .pulseViewModels()
        .assertTrue { it.resideInPackage("..feature..") }
}
```

## ルールに見えないもの

Konsist には ktlint のルールセットや detekt のプラグインのような読み込みの仕組みがありません。アーティファクト自身が勝手に走ることはできないので、呼び出すテストはプロジェクト側で書くことになります。

Konsist が読むのはコンパイル済みのクラスではなくソースコードです。ライブラリ側の基底クラスを経由して `PulseViewModel` に到達する ViewModel は、その基底クラスが走査対象のソースにないため認識できません。

Konsist は Gradle の build script も読まないので、モジュールの検査は import から依存を見つけます。build script で宣言していても、一度も import していない依存は見えません。

対象が 0 件のときは成功します。PulsePlatform の型がまだないモジュールでも落ちないので、検査するコードより先にテストを置けます。

## 次のステップ

- [UI の構成](/ja/guide/rules/ui) — PulsePlatform の中心にあるルール
- [AI エージェントと作る](/ja/guide/ai-agents) — エージェント自身に作業をルールで確かめさせる
- [ルール API](/ja/api/rules) — すべてのアサートとクエリのシグネチャ
