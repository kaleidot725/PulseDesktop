package jp.kaleidot725.pulse.architecture

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.declaration.KoFunctionDeclaration
import com.lemonappdev.konsist.api.verify.assertTrue

/**
 * Checks where the text a user reads comes from, so the app can be translated without touching its code.
 *
 * Every module that draws — a feature, `core/ui`, `core/designsystem` — keeps its text in its own string resources,
 * with Compose Multiplatform's `composeResources` and `stringResource(Res.string.…)`, and a translation is a
 * `values-<language>` directory beside them. The domain holds no text at all: an entity that needs a label is given one
 * where it is drawn.
 *
 * ```kotlin
 * class PulseResourceConventionTest {
 *     @Test
 *     fun `follows the PulseDesktop resource conventions`() {
 *         Konsist.scopeFromProduction().assertPulseResourceConventions()
 *     }
 * }
 * ```
 */
public fun KoScope.assertPulseResourceConventions(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    assertDomainHoldsNoText(layout)
    assertComposablesTakeTextFromResources()
}

/**
 * The domain declares no string literal with a letter in it.
 *
 * A label in the domain — an enum's display name, a message an entity builds — is in one language for every screen
 * that shows it, and translating it means editing the domain. The domain names things with types instead, and the
 * module that draws an entity maps it to a string resource. A literal without a letter, such as a separator, is not
 * text and passes.
 */
public fun KoScope.assertDomainHoldsNoText(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    files
        .filter { it.layerIn(layout) == ModuleLayer.Domain }
        .assertTrue(
            testName = "the domain holds no text",
            additionalMessage = "Name the case with a type, and map it to a string resource in the module that draws it.",
        ) { file -> file.textLiterals().none { it.hasReadableText() } }
}

/**
 * A composable passes no string literal as text to read: `Text("…")`, or a `text`, `title`, `label`, `placeholder`
 * or `contentDescription` argument.
 *
 * A literal there is text only one language can read. Taking it from `stringResource` lets a `values-<language>`
 * directory translate it. A literal that only places values, such as `"$count"` or `"${a} / ${b}"`, has no words of
 * its own and passes, and so does anything inside a `@Preview`, which shows sample data rather than the app's text.
 */
public fun KoScope.assertComposablesTakeTextFromResources() {
    composableFiles()
        .flatMap { file -> file.composables() }
        .filter { !it.isPreview }
        .assertTrue(
            testName = "composables take their text from string resources",
            additionalMessage = "Put the text in composeResources and read it with `stringResource(Res.string.…)`.",
        ) { composable -> composable.displayedLiterals().none { it.hasReadableText() } }
}

/** The string literals a composable hands to a text parameter or to `Text`. */
private fun KoFunctionDeclaration.displayedLiterals(): List<String> =
    displayedLiteral.findAll(text.withoutComments()).map { it.groupValues[2] }.toList()

/** The string literals of a file, comments left out. */
private fun KoFileDeclaration.textLiterals(): List<String> =
    stringLiteral.findAll(text.withoutComments()).map { it.groupValues[1].ifEmpty { it.groupValues[2] } }.toList()

/** A literal is text when a letter is left once its `$value` and `${expression}` templates are taken out. */
private fun String.hasReadableText(): Boolean = replace(template, "").any { it.isLetter() }

private fun String.withoutComments(): String = replace(blockComment, "").replace(lineComment, "")

private val displayedLiteral =
    Regex("""(\bText\s*\(|\b(?:text|title|label|placeholder|contentDescription)\s*=)\s*"((?:[^"\\]|\\.)*)"""")

private val stringLiteral = Regex("\"\"\"([\\s\\S]*?)\"\"\"|\"((?:[^\"\\\\\\n]|\\\\.)*)\"")

private val template = Regex("""\$\{[^}]*}|\$\w+""")

private val blockComment = Regex("""/\*[\s\S]*?\*/""")

private val lineComment = Regex("""(?m)^\s*//.*$|\s//.*$""")
