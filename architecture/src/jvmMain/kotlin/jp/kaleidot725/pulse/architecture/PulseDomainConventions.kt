package jp.kaleidot725.pulse.architecture

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoClassDeclaration
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.declaration.KoInterfaceDeclaration
import com.lemonappdev.konsist.api.declaration.KoPropertyDeclaration
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.verify.assertTrue

/**
 * Checks every convention PulseDesktop expects of the domain layer: the repositories an app keeps its data in.
 *
 * A repository is a class or an interface whose name ends with `Repository` — an implementation is named after what it
 * is, such as `InMemoryCounterRepository`, rather than with an `Impl` suffix. The checks keep repositories where a
 * reader looks for them, keep them free of the UI, keep the UI from reaching them except where ViewModels are created,
 * and keep their state readable but not writable from outside.
 *
 * ```kotlin
 * class PulseDomainConventionTest {
 *     @Test
 *     fun `follows the PulseDesktop domain conventions`() {
 *         Konsist.scopeFromProduction().assertPulseDomainConventions()
 *     }
 * }
 * ```
 *
 * Each check is also public on its own, so a project that disagrees with one can call the rest.
 */
public fun KoScope.assertPulseDomainConventions() {
    assertRepositoriesLiveInARepositoryPackage()
    assertRepositoriesDoNotDependOnTheUi()
    assertOnlyScreensTakeRepositories()
    assertRepositoriesExposeReadOnlyState()
}

/**
 * A repository lives under a `repository` package.
 *
 * The view layer is found by package — `section` and `component` — and the data layer is found the same way. A
 * repository next to the screen that uses it is one a second screen will not find, and duplicate.
 */
public fun KoScope.assertRepositoriesLiveInARepositoryPackage() {
    pulseRepositoryClasses().assertTrue(
        testName = "repository classes live in a repository package",
        additionalMessage = "Move the repository under a `repository` package.",
    ) { it.resideInPackage("..repository..") }

    pulseRepositoryInterfaces().assertTrue(
        testName = "repository interfaces live in a repository package",
        additionalMessage = "Move the repository under a `repository` package.",
    ) { it.resideInPackage("..repository..") }
}

/**
 * A repository imports nothing from Compose or from PulseDesktop.
 *
 * The data outlives every screen that shows it, and a repository that knows about the UI can only be tested and
 * reused together with it. Dependencies point one way: the UI knows the ViewModel, the ViewModel knows the repository,
 * and the repository knows neither.
 */
public fun KoScope.assertRepositoriesDoNotDependOnTheUi() {
    pulseRepositoryFiles().assertTrue(
        testName = "repositories do not depend on the UI",
        additionalMessage = "Remove the Compose or PulseDesktop import; let a ViewModel turn the data into UI state.",
    ) { file -> file.imports.none { it.name.isUiImport() } }
}

/**
 * Only a screen takes a repository; a section or a component never does.
 *
 * The screen is where ViewModels are created, so it is where a repository is handed to one. Below the screen the UI
 * talks to a ViewModel, never to the data behind it — a section that takes a repository has a second way to change
 * the data that its ViewModel does not see.
 */
public fun KoScope.assertOnlyScreensTakeRepositories() {
    (pulseSections() + pulseComponents()).assertTrue(
        testName = "only screens take repositories",
        additionalMessage = "Pass the repository to the ViewModel in the screen, and give the section the ViewModel.",
    ) { file -> file.composables().none { it.hasRepositoryParameter() } }
}

/**
 * A repository exposes no `var` and no mutable collection or flow except privately.
 *
 * Whoever holds the repository can read its state, but only the repository changes it. A public `MutableStateFlow`
 * lets any caller write past the repository's own rules, and a `var` changes without anyone being told.
 */
public fun KoScope.assertRepositoriesExposeReadOnlyState() {
    pulseRepositoryClasses().assertTrue(
        testName = "repository classes expose read-only state",
        additionalMessage = "Keep the mutable value private and expose it as a `StateFlow` or `Flow`.",
    ) { repository -> repository.properties().none { it.isExposedMutableState() } }

    pulseRepositoryInterfaces().assertTrue(
        testName = "repository interfaces expose read-only state",
        additionalMessage = "Declare the property as a `val` of a read-only type such as `StateFlow`.",
    ) { repository -> repository.properties().none { it.isExposedMutableState() } }
}

/** Every class whose name ends with `Repository`. */
public fun KoScope.pulseRepositoryClasses(): List<KoClassDeclaration> = classes().withNameEndingWith("Repository")

/** Every interface whose name ends with `Repository`. */
public fun KoScope.pulseRepositoryInterfaces(): List<KoInterfaceDeclaration> = interfaces().withNameEndingWith("Repository")

/** Every file that declares a repository class or interface. */
public fun KoScope.pulseRepositoryFiles(): List<KoFileDeclaration> =
    (pulseRepositoryClasses().map { it.containingFile } + pulseRepositoryInterfaces().map { it.containingFile })
        .distinctBy { it.path }

private fun String.isUiImport(): Boolean = startsWith("androidx.compose.") || startsWith("jp.kaleidot725.pulse.")

private fun KoPropertyDeclaration.isExposedMutableState(): Boolean {
    if (hasPrivateModifier) return false
    if (isVar) return true
    // The declared type when there is one, otherwise the initializer: `mutableListOf()` has no type to read.
    val shape = type?.name ?: value ?: return false
    return shape.contains("mutable", ignoreCase = true)
}
