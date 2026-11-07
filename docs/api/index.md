# API Overview

PulsePlatform publishes one package per role. The rules live in `jp.kaleidot725.pulse.architecture` (the `architecture` artifact). The library lives in `jp.kaleidot725.pulse.mvi`, published as `mvi-desktop` for the desktop and `mvi-mobile` for Android and iOS. Navigation 3 support lives in `jp.kaleidot725.pulse.navigation`, published as `navigation-desktop` and `navigation-mobile`.

## Rules

The assertions your tests call, and the queries behind them. Every page of the [Rules API](/api/rules) applies to desktop and mobile code alike, except where a check says which.

| Assertion | Description |
|---|---|
| [`assertPulseMviConventions`](/api/rules#assertpulsemviconventions) | The next twelve checks, in order |
| [`assertStatesAreDataTypes`](/api/rules#assertstatesaredatatypes) | A `PulseState` is a `data class` or `data object` |
| [`assertMessagesAreSealedOrDataTypes`](/api/rules#assertmessagesaresealedordatatypes) | A message is a `sealed class` with `data class` / `data object` cases, or a single `data class` / `data object` |
| [`assertStatesAndMessagesLiveInContracts`](/api/rules#assertstatesandmessagesliveincontracts) | States and messages are in a `…Contracts.kt` beside their ViewModel or Container |
| [`assertViewModelsAndContainersAreNamedAfterTheirBase`](/api/rules#assertviewmodelsandcontainersarenamedaftertheirbase) | `…ViewModel` and `…Container` |
| [`assertViewModelsHoldTheirDataInTheirState`](/api/rules#assertviewmodelsholdtheirdataintheirstate) | No `var` property on a ViewModel or Container |
| [`assertViewModelsLiveInContents`](/api/rules#assertviewmodelsliveincontents) | A ViewModel lives under `content` |
| [`assertContainersLiveAtTheFeatureRoot`](/api/rules#assertcontainersliveatthefeatureroot) | A Container lives at the feature root |
| [`assertHostsComposeContents`](/api/rules#asserthostscomposecontents) | A host is named `…Host` and composes contents instead of binding a ViewModel |
| [`assertFeaturePackagesHaveARole`](/api/rules#assertfeaturepackageshavearole) | A file in a feature is at its root or under `content`, `section` or `component` |
| [`assertContentsBindAViewModel`](/api/rules#assertcontentsbindaviewmodel) | A content binds its ViewModel with `PulseContent` |
| [`assertSectionsAndComponentsArePure`](/api/rules#assertsectionsandcomponentsarepure) | Sections and components take data and callbacks only |
| [`assertSectionsAndComponentsHavePreviews`](/api/rules#assertsectionsandcomponentshavepreviews) | Every section and component has a `<Name>_Preview` |
| [`assertPulseMobileConventions`](/api/rules#assertpulsemobileconventions) | The checks for an app on `mvi-mobile`, in order |
| [`assertMobileFeaturesHaveOneViewModel`](/api/rules#assertmobilefeatureshaveoneviewmodel) | A mobile ViewModel is at its feature root and alone there |
| [`assertScreensBindTheirViewModel`](/api/rules#assertscreensbindtheirviewmodel) | A mobile feature's root composable is a `…Screen` that binds its ViewModel |
| [`assertMobileFeaturePackagesHaveARole`](/api/rules#assertmobilefeaturepackageshavearole) | A file in a mobile feature is at its root or under `section` or `component` |
| [`assertPulseDomainConventions`](/api/rules#assertpulsedomainconventions) | A repository exposes flows and returns `Result<V, E>` from one-shot calls; a use case lives in `usecase` and is more than one line |
| [`assertPulseModuleConventions`](/api/rules#assertpulsemoduleconventions) | Modules depend on each other the way Clean Architecture asks, and the domain, data and design system are split into packages |
| [`assertPulseKoinConventions`](/api/rules#assertpulsekoinconventions) | Koin modules are declared in the app module |
| [`assertPulseResourceConventions`](/api/rules#assertpulseresourceconventions) | The domain holds no text, and composables read their text from string resources |
| [`PulseModuleLayout`](/api/rules#pulsemodulelayout) | Which module names play which layer |

## Library

`PulseViewModel`, `PulseContent` and the three markers below are in both `mvi-desktop` and `mvi-mobile`, and the two Navigation 3 composables are in both `navigation-desktop` and `navigation-mobile`. On mobile, `PulseViewModel` takes three type parameters instead of five.

| Declaration | Description |
|---|---|
| [`PulseViewModel`](/api/pulse-viewmodel) | Abstract base class that holds one UI state, handles actions and emits events |
| [`PulseContent`](/api/composables#pulsecontent) | Observes a ViewModel; provides the state and an action dispatcher |
| [`PulseState`](/api/interfaces#pulsestate) | Marks a class as a ViewModel's UI state |
| [`PulseAction`](/api/interfaces#pulseaction) | Marks a class as a user action |
| [`PulseEvent`](/api/interfaces#pulseevent) | Marks a class as a one-time side effect |
| [`rememberPulseViewModel`](/api/navigation3#rememberpulseviewmodel) | ViewModel scoped to a `ViewModelStoreOwner` |
| [`rememberPulseNavEntryDecorators`](/api/navigation3#rememberpulsenaventrydecorators) | `NavDisplay` decorators that scope ViewModels to a back stack entry |

## Desktop

These are in `mvi-desktop` and `navigation-desktop` only.

| Declaration | Description |
|---|---|
| [`PulseContainer`](/api/pulse-container) | Coordinates the ViewModels of a window |
| [`PulseHost`](/api/composables#pulsehost) | Scopes a Container to a subtree; enables refresh and broadcast callbacks |
| [`PulseBroadcast`](/api/interfaces#pulsebroadcast) | Marks a class as a Container broadcast message |
| [`PulseUnicast`](/api/interfaces#pulseunicast) | Marks a class as a child-to-parent unicast message |
| [`rememberPulseContainer`](/api/navigation3#rememberpulsecontainer) | Container scoped to a `ViewModelStoreOwner` |
