---
layout: home

hero:
  name: PulsePlatform
  text: デスクトップ、Android、iOS の Compose のフレームワーク
  tagline: ウィンドウを Host・Content・Section・Component に分け、Content ごとに ViewModel を持たせ、1 つの Container でまとめて動かします。
  actions:
    - theme: brand
      text: はじめる
      link: /ja/guide/getting-started
    - theme: alt
      text: PulsePlatform とは
      link: /ja/guide/
    - theme: alt
      text: GitHub で見る
      link: https://github.com/kaleidot725/PulsePlatform

features:
  - icon: 🪟
    title: ウィンドウのための設計
    details: デスクトップのウィンドウには、いくつものパネルが同時に並びます。ViewModel は画面ごとではなく、Content ごとに持たせます。
  - icon: 🧩
    title: Host・Content・Section・Component
    details: ウィンドウの分け方が決まっています。Host が Content を並べ、Content が ViewModel を 1 つ束ね、Section と Component はデータとコールバックだけを受け取ります。
  - icon: 📡
    title: Content どうしの連携
    details: Container がすべての Content に Broadcast し、Content は Unicast で Container に伝えます。Refresh は状態を保ったまま View を作り直します。
  - icon: 🔁
    title: 単方向の状態
    details: State・Action・Event による MVI です。状態は update で変わり、StateFlow として UI に届きます。
  - icon: 🧭
    title: Navigation 3 の寿命
    details: navigation アーティファクトが ViewModel と Container の寿命をバックスタックのエントリに結びつけます。覆われている間もルートの状態は残ります。
  - icon: ✅
    title: 規約をテストで守る
    details: architecture アーティファクトが、状態の規約、Host・Content・Section・Component の分け方、Repository、モジュール間の依存、Koin を Konsist で検査します。自分のテストに 1 本足すだけです。
---
