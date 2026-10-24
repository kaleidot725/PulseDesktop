package jp.kaleidot725.pulse.architecture

/**
 * Which modules play which part in [assertPulseModuleConventions].
 *
 * Each layer is a list of path patterns matched against the end of a module's directory, with `*` standing for exactly
 * one directory. `core/data` matches `<root>/core/data`, and `feature/&#42;` matches every directory directly under
 * `feature`. The layers are tried in the order they are declared here, so a broad pattern such as `core/&#42;` in [support]
 * only takes the modules no earlier layer claimed. A module that matches nothing is not checked.
 *
 * The default is Now in Android's names. A project with names of its own passes a copy:
 *
 * ```kotlin
 * val layout =
 *     PulseModuleLayout(
 *         app = listOf("desktop"),
 *         features = listOf("features/&#42;"),
 *         model = listOf("shared/entity"),
 *         domain = listOf("shared/usecase"),
 *         data = listOf("shared/repository"),
 *         dataSources = listOf("shared/db", "shared/api"),
 *         sharedUi = listOf("shared/widget"),
 *         support = listOf("shared/&#42;"),
 *     )
 *
 * Konsist.scopeFromProduction().assertPulseModuleConventions(layout)
 * ```
 */
public data class PulseModuleLayout(
    /** The application, which wires everything together. */
    public val app: List<String> = listOf("app"),
    /**
     * One feature each. A trailing `api` or `impl` directory belongs to the feature above it, so `feature/foryou/api`
     * and `feature/foryou/impl` are one feature.
     */
    public val features: List<String> = listOf("feature/*", "feature/*/api", "feature/*/impl"),
    /** The entities every layer shares. */
    public val model: List<String> = listOf("core/model"),
    /** The use cases, and the repository interfaces they need. */
    public val domain: List<String> = listOf("core/domain"),
    /** The repository implementations. */
    public val data: List<String> = listOf("core/data"),
    /** The stores and services the repository implementations read. */
    public val dataSources: List<String> = listOf("core/database", "core/network", "core/datastore"),
    /** The composables shared between features. */
    public val sharedUi: List<String> = listOf("core/designsystem", "core/ui"),
    /** Everything else a layer may use, such as `core/common` or `core/testing`. */
    public val support: List<String> = listOf("core/*"),
) {
    public companion object {
        /** The module names of Now in Android. */
        public val NowInAndroid: PulseModuleLayout = PulseModuleLayout()
    }

    internal fun layerOf(module: String): ModuleLayer? =
        listOf(
            ModuleLayer.App to app,
            ModuleLayer.Feature to features,
            ModuleLayer.Model to model,
            ModuleLayer.Domain to domain,
            ModuleLayer.Data to data,
            ModuleLayer.DataSource to dataSources,
            ModuleLayer.SharedUi to sharedUi,
            ModuleLayer.Support to support,
        ).firstOrNull { (_, patterns) -> patterns.any { module.endsWithPattern(it) } }?.first

    /** The part of [directory] a pattern matched, which names the module in reports and in [pulseModule]. */
    internal fun moduleOf(directory: String): String? {
        val patterns = app + features + model + domain + data + dataSources + sharedUi + support
        val matched = patterns.firstOrNull { directory.endsWithPattern(it) } ?: return null
        val name = directory.split('/').takeLast(matched.split('/').size).joinToString("/")
        val isFeature = features.any { name.endsWithPattern(it) }
        return if (isFeature) name.removeSuffix("/api").removeSuffix("/impl") else name
    }
}

private fun String.endsWithPattern(pattern: String): Boolean {
    val segments = split('/')
    val wanted = pattern.split('/')
    if (wanted.size > segments.size) return false
    return segments.takeLast(wanted.size).zip(wanted).all { (segment, part) -> part == "*" || part == segment }
}
