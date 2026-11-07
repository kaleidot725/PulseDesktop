# Text

`assertPulseResourceConventions()` keeps an app translatable. Every module that draws — a feature, `core/ui`, `core/designsystem` — keeps its text in its own string resources, with Compose Multiplatform's `composeResources`, and reads it with `stringResource(Res.string.…)`. A translation is a `values-<language>` directory beside `values`. An Android-only app may use Android resources and `stringResource(R.string.…)` instead; the check looks at the literals, not at where the text comes from. It takes the same `PulseModuleLayout` as the module checks, and applies on the desktop and on mobile alike.

An entity that needs a label gets it in the module that draws it. The checks are in the table, and the demo labelling the position of an area follows it.

| Check | Rule | Why |
|---|---|---|
| The domain holds no text | no string literal in the domain contains a letter | a label in the domain is in one language for every screen that shows it. The domain names a case with a type, and the module that draws it maps it to a string resource |
| Composables read text from resources | a composable hands no literal to `Text("…")` or to a `text`, `title`, `label`, `placeholder` or `contentDescription` argument | a literal there can only be read in one language. A literal that only places values, such as `"$count"`, and anything inside a `@Preview` pass |

```kotlin
// feature/count — the domain's enum has no label of its own
internal val PulseAreaPosition.label: StringResource
    get() =
        when (this) {
            PulseAreaPosition.TopLeft -> Res.string.position_top_left
            PulseAreaPosition.TopRight -> Res.string.position_top_right
            PulseAreaPosition.BottomLeft -> Res.string.position_bottom_left
            PulseAreaPosition.BottomRight -> Res.string.position_bottom_right
        }
```

## Next steps

- [Rules](/guide/rules/) — set up the test and choose the rules
