# UI structure

These rules decide how a feature is split and what each part may hold. They are the core of PulsePlatform: the library has one piece for each role, and the rules check that every piece is where it belongs. Desktop apps run them with `assertPulseMviConventions()`, mobile apps with `assertPulseMobileConventions()`. Both run the same rules for states, messages, sections, components and previews; only the layout of a feature differs. A failure names the declaration or file and says how to fix it.

## States and messages

These rules are the same on the desktop and on mobile.

| Check | Rule | Why |
|---|---|---|
| States are data types | a `PulseState` is a `data class` or `data object` | `StateFlow` drops an emission equal to the last one, and Compose skips a composable whose arguments are equal. Without `equals` the screen recomposes on every update |
| Messages are sealed classes | an Action, Event, Broadcast or Unicast is a `sealed class`, and each case inside it is a `data class` or `data object`; a message with one case may be a single `data class` or `data object`, and an interface is never one | each one is consumed by a `when`. Sealing is what turns a new case into a compile error instead of a branch nobody wrote, and `data` lets a case compare and print by its value |
| States and messages live in Contracts | a `PulseState`, Action, Event, Broadcast or Unicast is in a `…Contracts.kt` file in the package of a ViewModel or Container | they are what a ViewModel or a Container agrees on, so a reader finds them in one file beside the class that uses them: `PulseAreaContracts.kt` beside `PulseAreaViewModel`, `PulseCountContracts.kt` beside `PulseCountContainer` |
| Names follow the base class | a `PulseViewModel` is named `…ViewModel`, a `PulseContainer` is named `…Container` | both are `ViewModel` subclasses to the compiler, so the name is what tells a reader which one this is |
| Data lives in the state | a ViewModel and a Container declare no `var` property | a `var` beside the state changes without an emission, so nothing recomposes |

## Desktop

A feature is split into four roles. The **host** is what a route shows: it creates the ViewModels and the Container and lays the contents out. A **content** is the part one ViewModel answers for and binds it with `PulseContent`. A **section** draws a part of the screen from a state and callbacks, and a **component** is a small building block. Sections and components are both pure; only their role differs.

The package decides which is which, so names stay free except the host's. The root of a feature is the package its `content` package sits in, and it holds the host (a file named `…Host`), the Container and the Container's Contracts. Each content has a package of its own under `content`, with its ViewModel and Contracts beside it. Sections and components may sit at the root of the feature or inside the one content that uses them, and those three are the only packages under the root. A pure helper the whole feature uses, such as a label read with `stringResource`, goes in `component`; a composable that only returns a value is not a view, so it needs no preview.

```
count/
├── PulseCountHost.kt          # host
├── PulseCountContainer.kt
├── PulseCountContracts.kt     # the Container's broadcasts and unicasts
├── component/                 # components shared by the feature
│   └── PulseCountHeader.kt
└── content/
    └── area/
        ├── PulseAreaContent.kt    # content
        ├── PulseAreaViewModel.kt
        ├── PulseAreaContracts.kt  # the ViewModel's state, actions and events
        ├── section/               # sections of this content
        │   └── PulseAreaSection.kt
        └── component/             # components of this content
            └── PulseAreaCell.kt
```

| Check | Rule | Why |
|---|---|---|
| ViewModels live in contents | a `PulseViewModel` is under `content`, outside `section` and `component` | a ViewModel answers for one content, so it sits beside the composable that binds it and the pure parts below never see it |
| Containers live at the feature root | a `PulseContainer` is in the package that holds `content` | the Container coordinates every content of the feature, so it sits beside the host that creates it |
| Hosts compose contents | a composable at the feature root is in a file named `…Host` and does not call `PulseContent` | binding a ViewModel in the host instead of a content is how a host grows into the file that knows everything |
| Features have only their packages | inside a feature, every file is at the root or under `content`, `section` or `component`; generated code under `build/generated` is skipped | a package of its own, such as `text`, is one more place nobody decided on |
| Contents bind a ViewModel | a file with a composable under `content`, outside `section` and `component`, calls `PulseContent` | the content is the only place a ViewModel and the UI meet. A file that binds nothing is a section in the wrong package |
| Sections and components are pure | a composable under `section` or `component` takes no `…ViewModel` or `…Container` parameter, and calls neither `PulseContent` nor `PulseHost` | data and callbacks are what make them previewable on their own and reusable |
| Sections and components have previews | every public composable under `section` or `component` has a matching `@Preview` | a preview is all a pure composable needs to be seen on its own, and it is the first thing that breaks when the composable does |

## Mobile

An app built on [`mvi-mobile`](/guide/mobile) calls `assertPulseMobileConventions()` in place of `assertPulseMviConventions()`. A phone shows one screen at a time and `mvi-mobile` has no Container, so a feature is one **screen** and one ViewModel, with no host and no `content` package. The root of a feature is the package its ViewModel sits in. The screen sits there too, in a file named `…Screen`, and binds the ViewModel with `PulseContent`. Sections and components work as on the desktop, and `section` and `component` are the only packages under the root. The layout checks in the table replace the desktop's; everything else on this page applies unchanged.

```
counter/
├── CounterScreen.kt       # screen: binds CounterViewModel with PulseContent
├── CounterViewModel.kt
├── CounterContracts.kt    # the ViewModel's state, actions and events
├── section/
│   └── CounterSection.kt
└── component/
    └── CounterBadge.kt
```

| Check | Rule | Why |
|---|---|---|
| Features have one ViewModel | a `PulseViewModel` is outside `section`, `component` and `content`, and no other ViewModel shares its package | one screen is one ViewModel; a second one is a second screen without a feature of its own |
| Screens bind their ViewModel | a composable at the feature root is in a file named `…Screen` and calls `PulseContent`, and every ViewModel has such a screen beside it | the screen is what a route shows and the only place the ViewModel and the UI meet |
| Features have only their packages | inside a feature, every file is at the root or under `section` or `component`; generated code is skipped | a package of its own is one more place nobody decided on |

## Previews

A preview matches its section or component by name: `<Name>_Preview`, or `<Name>_Preview_<Condition>` for a particular state. One match is enough, and one with several states can have one per condition. A preview named any other way does not count, which is how a renamed composable left without its preview is caught.

| Preview | Matches `PulseTextButton` |
|---|---|
| `PulseTextButton_Preview` | yes |
| `PulseTextButton_Preview_Disabled` | yes |
| `PulseTextButton_Preview_Disabled_Dark` | yes |
| `PulseTextButtonPreview` | no |

## Next steps

- [Domain and data](/guide/rules/domain) — repositories and use cases
- [Rules API](/api/rules) — the signature of every assertion and query
