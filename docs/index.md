---
layout: home

hero:
  name: PulseDesktop
  text: A framework for Compose Desktop apps
  tagline: Split a window into a host, contents, sections and components. Give each content its own ViewModel, and let one Container keep them working together.
  actions:
    - theme: brand
      text: Get Started
      link: /guide/getting-started
    - theme: alt
      text: What is PulseDesktop?
      link: /guide/
    - theme: alt
      text: View on GitHub
      link: https://github.com/kaleidot725/PulseDesktop

features:
  - icon: 🪟
    title: Designed for windows
    details: A desktop window shows many panels at once. Each content gets its own ViewModel, rather than each screen getting one.
  - icon: 🧩
    title: Host, Content, Section, Component
    details: A fixed way to split a window. A host lays out contents, a content binds one ViewModel, and sections and components take data and callbacks.
  - icon: 📡
    title: Contents that work together
    details: The Container broadcasts to every content, and a content reports up with Unicast. Refresh rebuilds the view without losing state.
  - icon: 🔁
    title: One-way state
    details: MVI with State, Action and Event. State changes through update and reaches the UI as a StateFlow.
  - icon: 🧭
    title: Navigation 3 lifetimes
    details: The navigation artifact ties ViewModels and Containers to a back stack entry, so a route keeps its state while it is covered.
  - icon: ✅
    title: Conventions you can test
    details: The architecture artifact checks the state rules, the host, content, section and component split, repositories, module dependencies and Koin with Konsist, from one test in your own suite.
---
