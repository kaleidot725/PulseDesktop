---
layout: home

hero:
  name: PulsePlatform
  text: AI エージェントが作る Compose UI のためのルールと部品
  tagline: テストで実行するルールと、どの画面も同じ形にするライブラリ。デスクトップにも、Android と iOS にも。
  actions:
    - theme: brand
      text: PulsePlatform とは
      link: /ja/guide/
    - theme: alt
      text: デスクトップのクイックスタート
      link: /ja/guide/getting-started
    - theme: alt
      text: モバイルのクイックスタート
      link: /ja/guide/getting-started-mobile

features:
  - icon: ✅
    title: 実行できるルール
    details: UI の構成、State、Repository、モジュール間の依存、Koin、文言を、自分のテストスイートの 1 本のテストで検査します。失敗するとファイルの名前と直し方が示されます。
  - icon: 🤖
    title: AI エージェントのために
    details: エージェントはコードを書き、テストを実行し、何をどこへ動かすかを読みます。「アーキテクチャに従って」を、通すべき検査に変えます。
  - icon: 🧩
    title: どの画面も同じ形に
    details: Section と Component はデータとコールバックだけを受け取り、ViewModel を結びつける場所は 1 つです。ルールが求める部品をライブラリが持っているので、正しい構成がいちばん書きやすい構成になります。
  - icon: 🪟
    title: デスクトップのウィンドウ
    details: ウィンドウにはいくつものパネルが同時に並びます。Content ごとに ViewModel を持ち、Container がその間で Broadcast を届け、状態を保ったまま View を作り直します。
  - icon: 📱
    title: Android と iOS
    details: スマートフォンでは 1 つの画面に ViewModel が 1 つです。mvi-mobile はそれだけを持ち、Android と iOS の Compose Multiplatform で、同じルールのもとで動きます。
  - icon: 🔁
    title: 一方向の状態
    details: State・Action・Event による MVI です。State は update で変わり、StateFlow として UI に届きます。どのプラットフォームでも同じです。
---
