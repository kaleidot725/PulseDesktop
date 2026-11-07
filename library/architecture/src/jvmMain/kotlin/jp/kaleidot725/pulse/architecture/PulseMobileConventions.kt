package jp.kaleidot725.pulse.architecture

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.verify.assertTrue

/**
 * Checks every convention PulsePlatform expects of a mobile app built on `mvi-mobile`.
 *
 * A phone shows one screen at a time and `mvi-mobile` has no Container, so a feature is one screen and one ViewModel.
 * There is no host and no `content` package; the screen binds the ViewModel itself:
 *
 * ```
 * counter/
 * ├── CounterScreen.kt        # binds CounterViewModel with PulseContent
 * ├── CounterViewModel.kt
 * ├── CounterContracts.kt     # the ViewModel's state, actions and events
 * ├── section/                # pure: a part of the screen, drawn from a state and callbacks
 * └── component/              # pure: a small building block
 * ```
 *
 * The root of a feature is the package its ViewModel sits in, and `section` and `component` are the only packages under
 * it. The rules for states, messages, ViewModels, sections, components and previews are the desktop's.
 *
 * ```kotlin
 * class PulseMobileConventionTest {
 *     @Test
 *     fun `follows the PulsePlatform mobile conventions`() {
 *         Konsist.scopeFromProduction().assertPulseMobileConventions()
 *     }
 * }
 * ```
 *
 * Each check is also public on its own, so a project that disagrees with one can call the rest.
 */
public fun KoScope.assertPulseMobileConventions() {
    assertStatesAreDataTypes()
    assertMessagesAreSealedOrDataTypes()
    assertStatesAndMessagesLiveInContracts()
    assertViewModelsAndContainersAreNamedAfterTheirBase()
    assertViewModelsHoldTheirDataInTheirState()
    assertMobileFeaturesHaveOneViewModel()
    assertScreensBindTheirViewModel()
    assertMobileFeaturePackagesHaveARole()
    assertSectionsAndComponentsArePure()
    assertSectionsAndComponentsHavePreviews()
}

/**
 * A ViewModel sits at the root of its feature, outside any `section`, `component` or `content` package, and the
 * feature has no other.
 *
 * One screen is one ViewModel. A second one in the same package is a second screen sharing the first one's package,
 * and a ViewModel under `section` or `component` makes a pure part of the screen own state.
 */
public fun KoScope.assertMobileFeaturesHaveOneViewModel() {
    pulseViewModels().assertTrue(
        testName = "mobile ViewModels live at the root of their feature",
        additionalMessage = "Move the ViewModel out of `section`, `component` or `content`, beside the screen that binds it.",
    ) { !it.containingFile.isPure && !it.containingFile.hasPackage("..content..") }

    val perPackage = pulseViewModels().groupBy { it.containingFile.packagee?.name }
    pulseViewModels().assertTrue(
        testName = "a mobile feature has one ViewModel",
        additionalMessage = "Give each ViewModel a feature package of its own, with the screen that binds it.",
    ) { perPackage[it.containingFile.packagee?.name].orEmpty().size == 1 }
}

/**
 * A composable at the root of a mobile feature is its screen: the file is named `…Screen` and binds the ViewModel
 * beside it with `PulseContent`. Every ViewModel has such a screen.
 *
 * The screen is what a route shows and the only place the ViewModel and the UI meet. It hands the state and `onAction`
 * to the sections, and turns the events that leave the screen into callbacks.
 */
public fun KoScope.assertScreensBindTheirViewModel() {
    pulseScreens().assertTrue(
        testName = "screens are named Screen",
        additionalMessage = "Name the file `…Screen`, or move the composable into `section` or `component`.",
    ) { it.name.endsWith("Screen") }

    pulseScreens().assertTrue(
        testName = "screens bind their ViewModel with PulseContent",
        additionalMessage = "Call `PulseContent` with the feature's ViewModel here, or move the composable into `section` or `component`.",
    ) { it.text.contains("PulseContent(") }

    val screens = pulseScreens()
    pulseViewModels().assertTrue(
        testName = "every mobile ViewModel has a screen",
        additionalMessage = "Add a `…Screen` beside the ViewModel that binds it with `PulseContent`.",
    ) { viewModel -> screens.any { it.packagee?.name == viewModel.containingFile.packagee?.name } }
}

/**
 * Inside a mobile feature, every file sits at its root or under a `section` or `component` package.
 *
 * Those are the only packages a mobile feature has, so a reader knows where to look before opening one. Code a build
 * generates, such as the `Res` class of Compose resources, is not the feature's own and is skipped.
 */
public fun KoScope.assertMobileFeaturePackagesHaveARole() {
    val roots = pulseMobileFeatureRoots()
    files
        .filter { file -> !file.isGenerated && roots.any { root -> file.packagee?.name?.startsWith("$root.") == true } }
        .assertTrue(
            testName = "files in a mobile feature are at its root or under section or component",
            additionalMessage = "Move the file into a `section` or `component` package.",
        ) { it.isPure }
}

/** The packages a ViewModel sits in, outside `section` and `component`: the root of each mobile feature. */
public fun KoScope.pulseMobileFeatureRoots(): Set<String> =
    pulseViewModels()
        .map { it.containingFile }
        .filter { !it.isPure }
        .mapNotNull { it.packagee?.name }
        .toSet()

/** Every file with a composable at the root of a mobile feature. */
public fun KoScope.pulseScreens(): List<KoFileDeclaration> {
    val roots = pulseMobileFeatureRoots()
    return viewFiles().filter { it.packagee?.name in roots }
}
