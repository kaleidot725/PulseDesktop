package jp.kaleidot725.pulse.demo.core.domain.model

enum class PulseAreaPosition {
    TopLeft,
    TopRight,
    BottomLeft,
    BottomRight,
    ;

    val neighbors: Set<PulseAreaPosition>
        get() =
            when (this) {
                TopLeft -> setOf(TopRight, BottomLeft)
                TopRight -> setOf(TopLeft, BottomRight)
                BottomLeft -> setOf(TopLeft, BottomRight)
                BottomRight -> setOf(TopRight, BottomLeft)
            }
}
