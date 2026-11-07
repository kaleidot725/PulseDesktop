# Domain and data

These rules keep the layer below the UI predictable, so a ViewModel always gets data in the same shapes. `assertPulseDomainConventions()` runs them, on the desktop and on mobile alike.

## Repositories

`assertPulseDomainConventions()` checks how a repository hands out its data. A repository is a class or an interface whose name ends with `Repository`.

| Check | Rule | Why |
|---|---|---|
| Values to follow are flows | a public property is a cold `Flow` or a `StateFlow` | whoever reads it keeps following it. A `SharedFlow` drops what came before the collector, a public `MutableStateFlow` lets the caller write past the repository, and a plain value is stale once read |
| One-shot calls return a result | a public function that returns a value returns a `Flow`, a `StateFlow` or kotlin-result's `Result<V, E>` | a one-shot call can fail, and `Result<V, E>` puts the failure and its kinds in the type. `kotlin.Result` has no error type, so it does not count |

### What a repository may do

These are allowed and are not counted as data the repository hands out.

- A function that returns nothing is a command and is not checked
- A property holding another repository, or a function such as one a data module exposes, is what the repository uses
- A private `MutableStateFlow` behind a public `StateFlow` is how the repository changes its own state

## Use cases

A ViewModel may call a repository directly, so a use case is optional. Write one only when it has work of its own, such as combining two flows or deciding whether to act. `assertPulseDomainConventions()` checks the use cases there are. A use case is a class whose name ends with `UseCase`.

| Check | Rule | Why |
|---|---|---|
| Use cases have a package | a use case is in a `usecase` package | the entities, the repository interfaces and the use cases are read for different reasons, so each has its own place |
| Use cases do work of their own | no public function of a use case is a single line | a one-line use case only hands the call on to a repository. It is a class to read and wire for a call the ViewModel can make itself |

## Next steps

- [Modules and Koin](/guide/rules/modules) — where the domain and the data layer live
