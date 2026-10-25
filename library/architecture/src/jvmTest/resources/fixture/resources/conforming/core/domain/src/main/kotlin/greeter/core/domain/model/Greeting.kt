package greeter.core.domain.model

/** A greeting, named by its kind rather than by the words a screen shows for it. "Hello" lives in the feature. */
enum class GreetingKind { Morning, Evening }

data class Greeting(val kind: GreetingKind, val count: Int) {
    // The separator is not text a user reads.
    val key: String get() = "${kind.ordinal}-$count"
}
