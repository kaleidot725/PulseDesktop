package jp.kaleidot725.pulse.architecture

import androidx.compose.runtime.Composable
import com.lemonappdev.konsist.api.KoModifier
import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoClassDeclaration
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.declaration.KoFunctionDeclaration
import com.lemonappdev.konsist.api.declaration.KoInterfaceDeclaration
import com.lemonappdev.konsist.api.declaration.KoObjectDeclaration
import com.lemonappdev.konsist.api.ext.list.withParentOf
import com.lemonappdev.konsist.api.verify.assertTrue
import jp.kaleidot725.pulse.mvi.PulseAction
import jp.kaleidot725.pulse.mvi.PulseBroadcast
import jp.kaleidot725.pulse.mvi.PulseContainer
import jp.kaleidot725.pulse.mvi.PulseContent
import jp.kaleidot725.pulse.mvi.PulseEvent
import jp.kaleidot725.pulse.mvi.PulseState
import jp.kaleidot725.pulse.mvi.PulseUnicast
import jp.kaleidot725.pulse.mvi.PulseViewModel

/**
 * Checks every convention PulseDesktop expects of the code that uses it.
 *
 * The compiler already rejects the mistakes it can see: a ViewModel must implement `onAction`, and its message types
 * must be the ones its Container knows. What is left are the habits the types cannot express, and this is what they are
 * checked against.
 *
 * ```kotlin
 * class PulseMviConventionTest {
 *     @Test
 *     fun `follows the PulseDesktop conventions`() {
 *         Konsist.scopeFromProduction().assertPulseMviConventions()
 *     }
 * }
 * ```
 *
 * A feature is laid out as a **host**, the **contents** under it, and the **sections** and **components** they draw with:
 *
 * ```
 * counter/
 * ├── CounterHost.kt          # creates the ViewModels and the Container, lays the contents out
 * ├── CounterContainer.kt
 * ├── CounterContracts.kt     # the Container's broadcasts and unicasts
 * ├── content/
 * │   └── total/
 * │       ├── TotalContent.kt     # binds TotalViewModel with PulseContent
 * │       ├── TotalViewModel.kt
 * │       ├── TotalContracts.kt   # the ViewModel's state, actions and events
 * │       ├── section/            # pure composables only this content draws with
 * │       └── component/
 * ├── section/                # pure: a part of the screen, drawn from a state and callbacks
 * └── component/              # pure: a small building block
 * ```
 *
 * The packages decide the roles and the names are free, except for the host, whose file is named `…Host`. The root of a
 * feature is the package its `content` package sits in.
 *
 * Each check is also public on its own, so a project that disagrees with one can call the rest.
 */
public fun KoScope.assertPulseMviConventions() {
    assertStatesAreDataTypes()
    assertMessagesAreSealedOrDataTypes()
    assertStatesAndMessagesLiveInContracts()
    assertViewModelsAndContainersAreNamedAfterTheirBase()
    assertViewModelsHoldTheirDataInTheirState()
    assertViewModelsLiveInContents()
    assertContainersLiveAtTheFeatureRoot()
    assertHostsComposeContents()
    assertViewsHaveARole()
    assertContentsBindAViewModel()
    assertSectionsAndComponentsArePure()
    assertSectionsAndComponentsHavePreviews()
}

/**
 * A [PulseState] is a `data class` or a `data object`.
 *
 * State reaches the UI through a `StateFlow`, which drops an emission equal to the last one, and Compose skips a
 * composable whose arguments are equal to the previous ones. Both need `equals`, so a state without `data` recomposes
 * the screen on every update.
 */
public fun KoScope.assertStatesAreDataTypes() {
    pulseStateClasses().assertTrue(
        testName = "PulseState implementations are data classes",
        additionalMessage = "Add `data` so equal states compare equal and Compose can skip the recomposition.",
    ) { it.hasModifier(KoModifier.DATA) }

    pulseStateObjects().assertTrue(
        testName = "PulseState implementations are data objects",
        additionalMessage = "Add `data` so the object prints and compares as a state rather than as an identity.",
    ) { it.hasModifier(KoModifier.DATA) }
}

/**
 * A [PulseAction], [PulseEvent], [PulseBroadcast] or [PulseUnicast] is a `sealed class`, and every case inside it is a
 * `data class` or a `data object`. A message with a single case may instead be one `data class` or `data object` on its
 * own.
 *
 * Every one of them is consumed by a `when`, in `onAction`, `onReceive`, `onReceived` or an event handler. Sealing the
 * type is what makes that `when` exhaustive, so adding a case turns into a compile error instead of a branch nobody
 * wrote, and `data` is what lets a case print and compare by its value. A case may itself be a `sealed class` to group
 * cases further; its own cases are held to the same rule.
 *
 * A sealed interface would seal the type as well, but it is left out so that messages keep one shape: an interface, or a
 * class or object without `data` implementing the marker, is reported even when it would compile.
 */
public fun KoScope.assertMessagesAreSealedOrDataTypes() {
    (pulseMessageInterfaces() + pulseMessageClasses() + pulseMessageObjects()).assertTrue(
        testName = "PulseDesktop messages are sealed classes",
        additionalMessage =
            "Declare the message as a `sealed class` with `data class` or `data object` cases, or as a single `data class` or " +
                "`data object`.",
    ) { declaration ->
        when (declaration) {
            is KoClassDeclaration -> declaration.hasModifier(KoModifier.SEALED) || declaration.hasModifier(KoModifier.DATA)
            is KoObjectDeclaration -> declaration.hasModifier(KoModifier.DATA)
            else -> false
        }
    }

    val (caseClasses, caseObjects) = messageCases()
    (caseClasses + caseObjects).assertTrue(
        testName = "PulseDesktop message cases are data types",
        additionalMessage = "Add `data`, or `sealed` when the case groups cases of its own.",
    ) { it.hasModifier(KoModifier.DATA) || (it is KoClassDeclaration && it.hasModifier(KoModifier.SEALED)) }
}

/**
 * A [PulseState], [PulseAction], [PulseEvent], [PulseBroadcast] or [PulseUnicast] lives in a `…Contracts.kt` file in the
 * package of a ViewModel or a Container.
 *
 * These types are what a ViewModel or a Container agrees on with the UI and with each other, so a reader finds them in
 * one file beside the class that uses them: `TotalContracts.kt` beside `TotalViewModel`, `CounterContracts.kt` beside
 * `CounterContainer`. Only the type that implements the marker is checked; the cases inside a sealed class are in the
 * same file.
 */
public fun KoScope.assertStatesAndMessagesLiveInContracts() {
    val owners = (pulseViewModels() + pulseContainers()).mapNotNull { it.containingFile.packagee?.name }.toSet()
    (pulseStateClasses() + pulseStateObjects() + pulseMessageInterfaces() + pulseMessageClasses() + pulseMessageObjects())
        .assertTrue(
            testName = "states and messages live in a Contracts file beside their ViewModel or Container",
            additionalMessage = "Move the type into the `…Contracts.kt` file beside the ViewModel or Container that uses it.",
        ) { it.containingFile.name.endsWith("Contracts") && it.containingFile.packagee?.name in owners }
}

/**
 * A [PulseViewModel] is named `…ViewModel` and a [PulseContainer] is named `…Container`.
 *
 * Which of the two a class is decides where it lives and how long: a Container coordinates ViewModels and is closed with
 * the store that holds it. The name is what says which one a reader is looking at.
 */
public fun KoScope.assertViewModelsAndContainersAreNamedAfterTheirBase() {
    pulseViewModels().assertTrue(
        testName = "PulseViewModel subclasses are named ViewModel",
        additionalMessage = "Rename the class so it ends with `ViewModel`.",
    ) { it.hasNameEndingWith("ViewModel") }

    pulseContainers().assertTrue(
        testName = "PulseContainer subclasses are named Container",
        additionalMessage = "Rename the class so it ends with `Container`.",
    ) { it.hasNameEndingWith("Container") }
}

/**
 * A [PulseViewModel] and a [PulseContainer] declare no `var` property.
 *
 * Everything the UI reads belongs in the state, where `update` publishes it to the `StateFlow`. A `var` beside the state
 * is data the UI cannot observe: it changes without an emission, and nothing recomposes.
 */
public fun KoScope.assertViewModelsHoldTheirDataInTheirState() {
    (pulseViewModels() + pulseContainers()).assertTrue(
        testName = "PulseDesktop ViewModels and Containers keep their data in their state",
        additionalMessage = "Move the `var` into the state and publish it with `update`.",
    ) { declaration -> declaration.properties().none { it.isVar } }
}

/**
 * A [PulseViewModel] lives under a `content` package, outside its `section` and `component` packages.
 *
 * A ViewModel answers for one content, so it sits beside the composable that binds it, and the sections and components
 * below it never see it.
 */
public fun KoScope.assertViewModelsLiveInContents() {
    pulseViewModels().assertTrue(
        testName = "ViewModels live in a content package",
        additionalMessage = "Move the ViewModel into `content/<name>`, beside the content that binds it.",
    ) { it.containingFile.isContent }
}

/**
 * A [PulseContainer] lives at the root of a feature, the package its `content` package sits in.
 *
 * The Container coordinates every ViewModel of the feature, so it sits beside the host that creates it rather than in
 * any one content.
 */
public fun KoScope.assertContainersLiveAtTheFeatureRoot() {
    val roots = pulseFeatureRoots()
    pulseContainers().assertTrue(
        testName = "Containers live at the root of their feature",
        additionalMessage = "Move the Container beside the host, in the package that holds `content`.",
    ) { it.containingFile.packagee?.name in roots }
}

/**
 * A composable at the root of a feature is its host: the file is named `…Host` and binds no ViewModel itself.
 *
 * The host is what a route shows. It creates the ViewModels and the Container, passes the Container's `refresh` and
 * `broadcast` around, and lays the contents out. Binding a ViewModel there instead of in a content is what grows it into
 * the file that knows everything.
 */
public fun KoScope.assertHostsComposeContents() {
    pulseHosts().assertTrue(
        testName = "hosts are named Host",
        additionalMessage = "Name the file `…Host`, or move the composable into `content`, `section` or `component`.",
    ) { it.name.endsWith("Host") }

    pulseHosts().assertTrue(
        testName = "hosts compose contents instead of binding a ViewModel",
        additionalMessage = "Move the `PulseContent` call into a composable under a `content` package.",
    ) { !it.text.contains("PulseContent(") }
}

/**
 * Inside a feature, a composable is the host at its root or sits under a `content`, `section` or `component` package.
 *
 * Those four are the only roles, so a composable in any other package of a feature is one whose role nobody decided. A
 * composable that returns a value, such as a label read with `stringResource`, draws nothing and may live anywhere.
 */
public fun KoScope.assertViewsHaveARole() {
    val roots = pulseFeatureRoots()
    viewFiles()
        .filter { file -> roots.any { root -> file.packagee?.name?.startsWith("$root.") == true } }
        .assertTrue(
            testName = "composables in a feature are a host, a content, a section or a component",
            additionalMessage = "Move the composable into a `content`, `section` or `component` package.",
        ) { it.isContent || it.isPure }
}

/**
 * A composable under a `content` package binds a ViewModel with [PulseContent].
 *
 * A content is the part of a screen that one ViewModel answers for, and the only place a ViewModel and the UI meet: it
 * takes the ViewModel and hands the state and `onAction` to a section. A content that never calls [PulseContent] is a
 * section in the wrong package.
 */
public fun KoScope.assertContentsBindAViewModel() {
    pulseContents().assertTrue(
        testName = "contents bind their ViewModel with PulseContent",
        additionalMessage = "Call `PulseContent` here, or move this composable to a `section` or `component` package.",
    ) { it.text.contains("PulseContent(") }
}

/**
 * A composable under a `section` or `component` package takes no ViewModel and binds nothing.
 *
 * Sections and components are pure: they take data and callbacks, which is what makes them previewable on their own. A
 * section draws a part of the screen from one content's state and a component is a small building block; the checks
 * are the same and only the role differs. Either may live at the root of a feature or inside one content.
 */
public fun KoScope.assertSectionsAndComponentsArePure() {
    val pure = pulseSections() + pulseComponents()
    pure.assertTrue(
        testName = "sections and components take no ViewModel or Container",
        additionalMessage = "Pass the state and the callbacks it needs instead of the ViewModel.",
    ) { file -> file.views().none { it.hasViewModelOrContainerParameter() } }

    pure.assertTrue(
        testName = "sections and components do not bind a ViewModel",
        additionalMessage = "Move the `PulseContent` or `PulseHost` call to the content or the host above.",
    ) { !it.text.contains("PulseContent(") && !it.text.contains("PulseHost(") }
}

/**
 * Every section and component has a preview named `<Name>_Preview`, or `<Name>_Preview_<Condition>` for the state it
 * shows — `PulseTextButton_Preview` or `PulseTextButton_Preview_Disabled`.
 *
 * A section or a component takes data and callbacks only, so a preview is all it needs to be seen on its own, and the
 * preview is the first thing that breaks when it does. One matching preview is enough; one with several states can have
 * one per condition. The name ties the preview to what it shows, so a rename shows up here when the preview is left
 * behind. Private composables, composables that return a value, and the previews themselves are not counted.
 */
public fun KoScope.assertSectionsAndComponentsHavePreviews() {
    val previews = functions().filter { it.isPreview }.map { it.name }
    (pulseSections() + pulseComponents())
        .flatMap { file -> file.views() }
        .filter { !it.isPreview && !it.hasPrivateModifier }
        .assertTrue(
            testName = "sections and components have previews",
            additionalMessage = "Add a @Preview named `<Name>_Preview`, or `<Name>_Preview_<Condition>`.",
        ) { view -> previews.any { it.isPreviewNameOf(view.name) } }
}

/** The packages a `content` package sits in: the root of each feature. */
public fun KoScope.pulseFeatureRoots(): Set<String> =
    files
        .mapNotNull { file ->
            val packages = file.packagee?.name?.split('.') ?: return@mapNotNull null
            val content = packages.indexOf("content")
            if (content > 0) packages.take(content).joinToString(".") else null
        }.toSet()

/** Every file with a composable at the root of a feature. */
public fun KoScope.pulseHosts(): List<KoFileDeclaration> {
    val roots = pulseFeatureRoots()
    return viewFiles().filter { it.packagee?.name in roots }
}

/** Every file with a composable under a `content` package, outside its `section` and `component` packages. */
public fun KoScope.pulseContents(): List<KoFileDeclaration> = viewFiles().filter { it.isContent }

/** Every file with a composable under a `section` package, except those under a `component` package inside it. */
public fun KoScope.pulseSections(): List<KoFileDeclaration> =
    viewFiles().filter { it.hasPackage("..section..") && !it.hasPackage("..component..") }

/** Every file with a composable under a `component` package. */
public fun KoScope.pulseComponents(): List<KoFileDeclaration> = viewFiles().filter { it.hasPackage("..component..") }

/** Every class that extends [PulseViewModel] directly. */
public fun KoScope.pulseViewModels(): List<KoClassDeclaration> = classes().withParentOf(PulseViewModel::class)

/** Every class that extends [PulseContainer] directly. */
public fun KoScope.pulseContainers(): List<KoClassDeclaration> = classes().withParentOf(PulseContainer::class)

/** Every class that implements [PulseState] directly. */
public fun KoScope.pulseStateClasses(): List<KoClassDeclaration> = classes().withParentOf(PulseState::class)

/** Every object that implements [PulseState] directly. */
public fun KoScope.pulseStateObjects(): List<KoObjectDeclaration> = objects().withParentOf(PulseState::class)

/** Every interface that implements one of the four message markers directly. */
public fun KoScope.pulseMessageInterfaces(): List<KoInterfaceDeclaration> = interfaces().withParentOf(messageMarkers)

/** Every class that implements one of the four message markers directly. */
public fun KoScope.pulseMessageClasses(): List<KoClassDeclaration> = classes().withParentOf(messageMarkers)

/** Every object that implements one of the four message markers directly. */
public fun KoScope.pulseMessageObjects(): List<KoObjectDeclaration> = objects().withParentOf(messageMarkers)

internal fun KoScope.composableFiles(): List<KoFileDeclaration> = files.filter { it.composables().isNotEmpty() }

/** Files with a composable that draws, as opposed to one that only returns a value. */
internal fun KoScope.viewFiles(): List<KoFileDeclaration> = files.filter { it.views().isNotEmpty() }

/** The composables that return nothing, which are the ones that draw; one that returns a value is a template. */
internal fun KoFileDeclaration.views(): List<KoFunctionDeclaration> =
    composables().filter { it.returnType?.name.let { type -> type == null || type == "Unit" } }

private val KoFileDeclaration.isPure: Boolean
    get() = hasPackage("..section..") || hasPackage("..component..")

private val KoFileDeclaration.isContent: Boolean
    get() = hasPackage("..content..") && !isPure

internal fun KoFileDeclaration.composables(): List<KoFunctionDeclaration> = functions().filter { it.hasAnnotationOf(Composable::class) }

/** `@Preview` from any package, and multipreview annotations such as `@PreviewLightDark`. */
internal val KoFunctionDeclaration.isPreview: Boolean
    get() = annotations.any { it.name.substringAfterLast('.').startsWith("Preview") }

private fun String.isPreviewNameOf(component: String): Boolean = Regex("${Regex.escape(component)}_Preview(_\\w+)?").matches(this)

private fun KoFunctionDeclaration.hasViewModelOrContainerParameter(): Boolean =
    parameters.any { it.type.name.endsWith("ViewModel") || it.type.name.endsWith("Container") }

/**
 * The classes and objects that extend a sealed message class, at any depth: a case that is a `sealed class` of its own
 * passes its cases on to the next round.
 */
private fun KoScope.messageCases(): Pair<List<KoClassDeclaration>, List<KoObjectDeclaration>> {
    val classes = classes()
    val objects = objects()
    val caseClasses = mutableListOf<KoClassDeclaration>()
    val caseObjects = mutableListOf<KoObjectDeclaration>()
    var parents = pulseMessageClasses().filter { it.hasModifier(KoModifier.SEALED) }.map { it.name }.toSet()
    val seen = parents.toMutableSet()
    while (parents.isNotEmpty()) {
        val newClasses = classes.filter { c -> c.name !in seen && c.parents().any { it.name in parents } }
        caseClasses += newClasses
        caseObjects += objects.filter { o -> o.parents().any { it.name in parents } }
        parents = newClasses.filter { it.hasModifier(KoModifier.SEALED) }.map { it.name }.toSet()
        seen += newClasses.map { it.name }
    }
    return caseClasses to caseObjects.distinct()
}

private val messageMarkers =
    listOf(
        PulseAction::class,
        PulseEvent::class,
        PulseBroadcast::class,
        PulseUnicast::class,
    )
