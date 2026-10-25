package jp.kaleidot725.pulse.architecture

/**
 * Which modules play which part in [assertPulseModuleConventions].
 *
 * Each layer is a list of path patterns matched against the end of a module's directory, with `*` standing for exactly
 * one directory: `core/data` matches `<root>/core/data`, and `feature/&#42;` matches every directory directly under
 * `feature`. The layers are tried in the order they are declared here, so a broad pattern such as `core/&#42;` in
 * [support] only takes the modules no earlier layer claimed. A module that matches nothing is not checked.
 *
 * The default follows the module names of Now in Android. A project with names of its own passes a copy:
 *
 * ```kotlin
 * val layout =
 *     PulseModuleLayout(
 *         app = listOf("desktop"),
 *         features = listOf("features/&#42;"),
 *         domain = listOf("shared/domain"),
 *         data = listOf("shared/data"),
 *         sharedUi = listOf("shared/widget"),
 *         support = listOf("shared/&#42;"),
 *         designSystem = listOf("shared/theme"),
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
    /**
     * The entities in `model`, the repository interfaces in `repository`, and the use cases, when there are any, in
     * `usecase`.
     */
    public val domain: List<String> = listOf("core/domain"),
    /**
     * The repository implementations in `repository`, the data sources they read in `local`, and the shapes those
     * sources store in `model`.
     */
    public val data: List<String> = listOf("core/data"),
    /** The composables shared between features that draw the domain's entities. */
    public val sharedUi: List<String> = listOf("core/ui"),
    /** Everything else a layer may use, such as `core/common` or `core/testing`. */
    public val support: List<String> = listOf("core/*"),
    /** The theme and the building blocks every screen is drawn with, which know nothing of the domain. */
    public val designSystem: List<String> = listOf("core/designsystem"),
) {
    public companion object {
        /** The module names of Now in Android. */
        public val NowInAndroid: PulseModuleLayout = PulseModuleLayout()
    }

    private val layers: List<Pair<ModuleLayer, List<String>>>
        get() =
            listOf(
                ModuleLayer.App to app,
                ModuleLayer.Feature to features,
                ModuleLayer.Domain to domain,
                ModuleLayer.Data to data,
                ModuleLayer.DesignSystem to designSystem,
                ModuleLayer.SharedUi to sharedUi,
                ModuleLayer.Support to support,
            )

    internal fun layerOf(directory: String): ModuleLayer? =
        layers.firstOrNull { (_, patterns) -> patterns.any { directory.endsWithPattern(it) } }?.first

    /** The part of [directory] a pattern matched, which names the module in reports and in [pulseModule]. */
    internal fun moduleOf(directory: String): String? {
        val matched = layers.flatMap { it.second }.firstOrNull { directory.endsWithPattern(it) } ?: return null
        val name = directory.split('/').takeLast(matched.split('/').size).joinToString("/")
        return if (layerOf(directory) == ModuleLayer.Feature) name.removeSuffix("/api").removeSuffix("/impl") else name
    }
}

private fun String.endsWithPattern(pattern: String): Boolean {
    val segments = split('/')
    val wanted = pattern.split('/')
    if (wanted.size > segments.size) return false
    return segments.takeLast(wanted.size).zip(wanted).all { (segment, part) -> part == "*" || part == segment }
}
