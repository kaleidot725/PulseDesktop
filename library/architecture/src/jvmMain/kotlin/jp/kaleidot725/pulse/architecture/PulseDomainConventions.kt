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
 * Checks how a repository hands out its data, and what earns a use case its place.
 *
 * A repository is a class or an interface whose name ends with `Repository`, and its public surface comes in two kinds:
 *
 * - a value to keep following is a cold `Flow` or a `StateFlow`
 * - a value to fetch once comes from a function that returns kotlin-result's `Result<V, E>`
 *
 * A function that returns nothing is a command and is left alone. A repository may hold other repositories and the
 * functions a data module exposes, so properties of those types are not counted as data it hands out.
 *
 * A use case is a class whose name ends with `UseCase`. A ViewModel may use a repository directly, so a use case is
 * written only when it has work of its own: it lives in a `usecase` package, and none of its functions is a single
 * line that hands the call on.
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
    assertUseCasesLiveInTheUseCasePackage()
    assertUseCasesDoMoreThanOneLine()
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

/** A use case lives in a `usecase` package, apart from the entities and the repository interfaces. */
public fun KoScope.assertUseCasesLiveInTheUseCasePackage() {
    pulseUseCases().assertTrue(
        testName = "use cases live in a usecase package",
        additionalMessage = "Move the use case into a package named `usecase`.",
    ) { it.resideInPackage("..usecase..") }
}

/**
 * No public function of a use case is a single line.
 *
 * A use case whose body is one line only hands the call on to a repository, and the ViewModel can make that call
 * itself. It is a file to read and a class to wire for nothing, so it is not written: a use case is for combining,
 * filtering or deciding something the repository does not.
 */
public fun KoScope.assertUseCasesDoMoreThanOneLine() {
    pulseUseCases().assertTrue(
        testName = "use cases do more than one line",
        additionalMessage = "Call the repository from the ViewModel instead, or give the use case work of its own.",
    ) { useCase -> useCase.publicFunctions().none { it.bodyLineCount() <= 1 } }
}

/** Every class whose name ends with `UseCase`. */
public fun KoScope.pulseUseCases(): List<KoClassDeclaration> = classes().withNameEndingWith("UseCase")

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

/**
 * The non-blank lines of a function's body: what follows `=` for an expression body, or what sits between the braces
 * for a block body. A function without a body counts as none.
 */
private fun KoFunctionDeclaration.bodyLineCount(): Int {
    val text = text
    var depth = 0
    var index = text.indexOf('(')
    if (index < 0) return 0
    while (index < text.length) {
        when (text[index]) {
            '(' -> depth++
            ')' -> if (--depth == 0) break
        }
        index++
    }
    val rest = text.substring(index + 1)
    val start = rest.indexOfFirst { it == '=' || it == '{' }
    if (start < 0) return 0
    val body =
        if (rest[start] == '=') {
            rest.substring(start + 1)
        } else {
            rest.substring(start + 1, rest.lastIndexOf('}').coerceAtLeast(start + 1))
        }
    return body.lines().count { it.isNotBlank() }
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
