---
layout: home

hero:
  name: PulseDesktop
  text: Compose Desktop のフレームワーク
  tagline: ウィンドウを Screen・Section・Component に分け、Section ごとに ViewModel を持たせ、1 つの Container でまとめて動かします。
  actions:
    - theme: brand
      text: はじめる
      link: /ja/guide/getting-started
    - theme: alt
      text: PulseDesktop とは
      link: /ja/guide/
    - theme: alt
      text: GitHub で見る
      link: https://github.com/kaleidot725/PulseDesktop

features:
  - icon: 🪟
    title: ウィンドウのための設計
    details: デスクトップのウィンドウには、いくつものパネルが同時に並びます。ViewModel は画面ごとではなく、Section ごとに持たせます。
  - icon: 🧩
    title: Screen・Section・Component
    details: ウィンドウの分け方が決まっています。Screen が Section を並べ、Section が ViewModel を 1 つ束ね、Component はデータとコールバックだけを受け取ります。
  - icon: 📡
    title: Section どうしの連携
    details: Container がすべての Section に Broadcast し、Section は Unicast で Container に伝えます。Refresh は状態を保ったまま View を作り直します。
  - icon: 🔁
    title: 単方向の状態
    details: State・Action・Event による MVI です。状態は update で変わり、StateFlow として UI に届きます。
  - icon: 🧭
    title: Navigation 3 の寿命
    details: navigation アーティファクトが ViewModel と Container の寿命をバックスタックのエントリに結びつけます。覆われている間もルートの状態は残ります。
  - icon: ✅
    title: 規約をテストで守る
    details: architecture アーティファクトが、状態の規約、Screen・Section・Component の分け方、Repository、モジュール間の依存、Koin を Konsist で検査します。自分のテストに 1 本足すだけです。
---
