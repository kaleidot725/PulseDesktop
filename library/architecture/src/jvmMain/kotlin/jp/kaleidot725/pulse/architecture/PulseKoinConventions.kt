package jp.kaleidot725.pulse.architecture

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.verify.assertTrue

/**
 * Checks how an app that uses Koin is put together.
 *
 * A project without Koin passes: there is nothing to check. Module names come from the same [PulseModuleLayout] as
 * [assertPulseModuleConventions], so a project with names of its own passes its layout here too.
 *
 * ```kotlin
 * class PulseKoinConventionTest {
 *     @Test
 *     fun `follows the PulseDesktop Koin conventions`() {
 *         Konsist.scopeFromProduction().assertPulseKoinConventions()
 *     }
 * }
 * ```
 */
public fun KoScope.assertPulseKoinConventions(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    assertKoinModulesLiveInTheApp(layout)
}

/**
 * Koin modules are declared in the app module and nowhere else.
 *
 * The app is the one module that knows every other, so it is the one place the whole graph can be read: which
 * implementation stands behind each domain interface, which use cases a ViewModel gets, what is a single and what is
 * made fresh. A feature or a core module that declares its own Koin module takes part of that decision away from the
 * app, and has to depend on Koin's DSL to do it.
 *
 * A file declares a Koin module when it imports `org.koin.dsl.module` or Koin Annotations' `@Module`. Resolving from
 * Koin — `koinInject`, `getKoin`, `parametersOf` — is not declaring a module and is allowed anywhere. Files outside the
 * layout are not checked.
 */
public fun KoScope.assertKoinModulesLiveInTheApp(layout: PulseModuleLayout = PulseModuleLayout.NowInAndroid) {
    pulseKoinModuleFiles()
        .filter { it.layerIn(layout) != null }
        .assertTrue(
            testName = "Koin modules live in the app module",
            additionalMessage = "Move this Koin module into the app module, next to the others.",
        ) { it.layerIn(layout) == ModuleLayer.App }
}

/** Every file that declares a Koin module, through the DSL or through Koin Annotations. */
public fun KoScope.pulseKoinModuleFiles(): List<KoFileDeclaration> =
    files.filter { file -> file.imports.any { it.name in koinModuleImports } }

private val koinModuleImports =
    setOf(
        "org.koin.dsl.module",
        "org.koin.dsl.*",
        "org.koin.core.annotation.Module",
        "org.koin.core.annotation.*",
    )
