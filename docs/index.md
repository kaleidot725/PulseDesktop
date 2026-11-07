---
layout: home

hero:
  name: PulsePlatform
  text: Rules and building blocks for Compose UIs that AI agents build
  tagline: A rule set your tests run, and a library that gives every screen the same shape. For the desktop and for Android and iOS.
  actions:
    - theme: brand
      text: What is PulsePlatform?
      link: /guide/
    - theme: alt
      text: Desktop quick start
      link: /guide/getting-started
    - theme: alt
      text: Mobile quick start
      link: /guide/getting-started-mobile

features:
  - icon: ✅
    title: Rules you can run
    details: The UI layout, state, repositories, module dependencies, Koin and text are checked by one test in your own suite. A failure names the file and says how to fix it.
  - icon: 🤖
    title: Made for AI agents
    details: An agent writes, runs the test, and reads what to move where. The rules turn "follow our architecture" into a check it can pass.
  - icon: 🧩
    title: One shape for every screen
    details: Sections and components take data and callbacks, and one place binds each ViewModel. The library has the pieces the rules expect, so the right structure is also the easy one.
  - icon: 🪟
    title: Desktop windows
    details: A window shows many panels at once. Each content has its own ViewModel, and a Container broadcasts between them and refreshes the view without losing state.
  - icon: 📱
    title: Android and iOS
    details: On a phone a screen is one ViewModel. mvi-mobile keeps only that, for Compose Multiplatform on Android and iOS, under the same rules.
  - icon: 🔁
    title: One-way state
    details: MVI with State, Action and Event. State changes through update and reaches the UI as a StateFlow, the same on every platform.
---
