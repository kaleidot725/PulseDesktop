package jp.kaleidot725.pulse.architecture

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoBaseDeclaration
import com.lemonappdev.konsist.api.declaration.KoClassDeclaration
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.declaration.KoFunctionDeclaration
import com.lemonappdev.konsist.api.declaration.KoInterfaceDeclaration
import com.lemonappdev.konsist.api.declaration.KoPropertyDeclaration
import com.lemonappdev.konsist.api.declaration.type.KoTypeDeclaration
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.provider.modifier.KoVisibilityModifierProvider
import com.lemonappdev.konsist.api.verify.assertTrue

/**
 * Checks how a repository hands out its data.
 *
 * A repository is a class or an interface whose name ends with `Repository`, and its public surface comes in two kinds:
 *
 * - a value to keep following is a cold `Flow` or a `StateFlow`
 * - a value to fetch once comes from a function that returns kotlin-result's `Result<V, E>`
 *
 * A function that returns nothing is a command and is left alone. A repository may hold other repositories and the
 * functions a data module exposes, so properties of those types are not counted as data it hands out.
 *
 * ```kotlin
 * class PulseDomainConventionTest {
 *     @Test
 *     fun `follows the PulseDesktop domain conventions`() {
 *         Konsist.scopeFromProduction().assertPulseDomainConventions()
 *     }
 * }
 * ```
 */
public fun KoScope.assertPulseDomainConventions() {
    assertRepositoriesExposeFlowsOrStateFlows()
    assertRepositoriesReturnResultsFromOneShotCalls()
}

/**
 * A repository's public property is a cold `Flow` or a `StateFlow`.
 *
 * Whoever reads the property keeps following it, so it has to be something that can be followed. A `SharedFlow` drops
 * what was sent before the collector arrived, a `MutableStateFlow` lets the caller write past the repository, and a
 * plain value is out of date as soon as it is read. A property holding another repository or a function is exempt:
 * that is what the repository uses, not what it hands out.
 */
public fun KoScope.assertRepositoriesExposeFlowsOrStateFlows() {
    pulseRepositories().assertTrue(
        testName = "repositories expose flows or state flows",
        additionalMessage = "Expose the value as a cold `Flow` or a `StateFlow`, and keep anything mutable private.",
    ) { repository -> repository.publicProperties().all { it.isFollowable() } }
}

/**
 * A repository's public function that returns a value returns a `Flow`, a `StateFlow`, or kotlin-result's
 * `Result<V, E>`.
 *
 * A one-shot call can fail, and the caller should not have to remember a `try`. `Result<V, E>` puts the failure in the
 * type, and the `E` says which failures there are — which `kotlin.Result` cannot. A function with no return value is a
 * command and is not checked.
 */
public fun KoScope.assertRepositoriesReturnResultsFromOneShotCalls() {
    pulseRepositories().assertTrue(
        testName = "repositories return results from one-shot calls",
        additionalMessage = "Return `com.github.michaelbull.result.Result<V, E>`, or a `Flow` to keep following.",
    ) { repository -> repository.publicFunctions().all { it.returnsFlowOrResult() } }
}

/** Every class and interface whose name ends with `Repository`, checked together so one report names them all. */
public fun KoScope.pulseRepositories(): List<KoBaseDeclaration> = pulseRepositoryClasses() + pulseRepositoryInterfaces()

/** Every class whose name ends with `Repository`. */
public fun KoScope.pulseRepositoryClasses(): List<KoClassDeclaration> = classes().withNameEndingWith("Repository")

/** Every interface whose name ends with `Repository`. */
public fun KoScope.pulseRepositoryInterfaces(): List<KoInterfaceDeclaration> = interfaces().withNameEndingWith("Repository")

private val KoVisibilityModifierProvider.isPublic: Boolean
    get() = !hasPrivateModifier && !hasProtectedModifier && !hasInternalModifier

/** A class may keep members to itself; everything an interface declares is public. */
private fun KoBaseDeclaration.publicProperties(): List<KoPropertyDeclaration> =
    when (this) {
        is KoClassDeclaration -> properties(includeNested = false).filter { it.isPublic }
        else -> (this as KoInterfaceDeclaration).properties(includeNested = false)
    }

private fun KoBaseDeclaration.publicFunctions(): List<KoFunctionDeclaration> =
    when (this) {
        is KoClassDeclaration -> functions(includeNested = false, includeLocal = false).filter { it.isPublic }
        else -> (this as KoInterfaceDeclaration).functions(includeNested = false, includeLocal = false)
    }

private fun KoPropertyDeclaration.isFollowable(): Boolean {
    val type = type ?: return false
    if (type.isFunctionType || type.baseName.endsWith("Repository")) return true
    return type.baseName in followableTypes
}

private fun KoFunctionDeclaration.returnsFlowOrResult(): Boolean {
    val type = returnType ?: return !hasExpressionBody
    return when (type.baseName) {
        "Unit" -> true
        in followableTypes -> true
        "Result" -> type.isKotlinResult(containingFile)
        else -> false
    }
}

private fun KoTypeDeclaration.isKotlinResult(file: KoFileDeclaration): Boolean =
    text.startsWith("$KOTLIN_RESULT_PACKAGE.Result") ||
        file.imports.any { it.name == "$KOTLIN_RESULT_PACKAGE.Result" || it.name == "$KOTLIN_RESULT_PACKAGE.*" }

/** `StateFlow<Int>` and `kotlinx.coroutines.flow.StateFlow<Int>` are both `StateFlow`. */
private val KoTypeDeclaration.baseName: String
    get() =
        text
            .substringBefore('<')
            .removeSuffix("?")
            .substringAfterLast('.')
            .trim()

private val followableTypes = setOf("Flow", "StateFlow")

private const val KOTLIN_RESULT_PACKAGE = "com.github.michaelbull.result"
