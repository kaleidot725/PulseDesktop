# API Overview

PulseDesktop exposes a small, focused API surface. One package per artifact: `mvi` is the core and lives in `jp.kaleidot725.pulse.mvi`, while the optional `navigation` and `architecture` artifacts live in `jp.kaleidot725.pulse.navigation` and `jp.kaleidot725.pulse.architecture`. Both are listed under [Extensions](#extensions).

## Classes

| Class | Description |
|---|---|
| [`PulseViewModel`](/api/pulse-viewmodel) | Abstract base class for managing UI state |
| [`PulseContainer`](/api/pulse-container) | Coordinates multiple ViewModels |

## Composables

| Composable | Description |
|---|---|
| [`PulseHost`](/api/composables#pulsehost) | Scopes a Container to a subtree; enables refresh and broadcast callbacks |
| [`PulseContent`](/api/composables#pulsecontent) | Observes a ViewModel; provides state and action dispatcher |

## Marker Interfaces

| Interface | Description |
|---|---|
| [`PulseState`](/api/interfaces#pulsestate) | Marks a class as a ViewModel's UI state |
| [`PulseAction`](/api/interfaces#pulseaction) | Marks a class as a user action |
| [`PulseEvent`](/api/interfaces#pulseevent) | Marks a class as a one-time side effect |
| [`PulseBroadcast`](/api/interfaces#pulsebroadcast) | Marks a class as a Container broadcast message |
| [`PulseUnicast`](/api/interfaces#pulseunicast) | Marks a class as a child-to-parent unicast message |

## Extensions

### Navigation 3

| Composable | Description |
|---|---|
| [`rememberPulseViewModel`](/api/navigation3#rememberpulseviewmodel) | ViewModel scoped to a `ViewModelStoreOwner` |
| [`rememberPulseContainer`](/api/navigation3#rememberpulsecontainer) | Container scoped to a `ViewModelStoreOwner` |
| [`rememberPulseNavEntryDecorators`](/api/navigation3#rememberpulsenaventrydecorators) | `NavDisplay` decorators that scope ViewModels to a back stack entry |

### Architecture checks

| Assertion | Description |
|---|---|
| [`assertPulseMviConventions`](/api/architecture#assertpulsemviconventions) | The next eight checks, in order |
| [`assertStatesAreDataTypes`](/api/architecture#assertstatesaredatatypes) | A `PulseState` is a `data class` or `data object` |
| [`assertMessagesAreSealedOrDataTypes`](/api/architecture#assertmessagesaresealedordatatypes) | A message interface is `sealed`; a class or object implementing one is `data` |
| [`assertViewModelsAndContainersAreNamedAfterTheirBase`](/api/architecture#assertviewmodelsandcontainersarenamedaftertheirbase) | `…ViewModel` and `…Container` |
| [`assertViewModelsHoldTheirDataInTheirState`](/api/architecture#assertviewmodelsholdtheirdataintheirstate) | No `var` property on a ViewModel or Container |
| [`assertSectionsBindOneViewModel`](/api/architecture#assertsectionsbindoneviewmodel) | A section binds its ViewModel with `PulseContent` |
| [`assertComponentsAreStateless`](/api/architecture#assertcomponentsarestateless) | A component takes data and callbacks only |
| [`assertScreensComposeSections`](/api/architecture#assertscreenscomposesections) | A screen composes sections instead of binding a ViewModel |
| [`assertComponentsHavePreviews`](/api/architecture#assertcomponentshavepreviews) | Every component has a `<Component>_Preview` |
| [`assertPulseDomainConventions`](/api/architecture#assertpulsedomainconventions) | A repository exposes flows and returns `Result<V, E>` from one-shot calls |
| [`assertPulseModuleConventions`](/api/architecture#assertpulsemoduleconventions) | Modules depend on each other the way Clean Architecture asks |
| [`assertPulseKoinConventions`](/api/architecture#assertpulsekoinconventions) | Koin modules are declared in the app module |
| [`PulseModuleLayout`](/api/architecture#pulsemodulelayout) | Which module names play which layer |
