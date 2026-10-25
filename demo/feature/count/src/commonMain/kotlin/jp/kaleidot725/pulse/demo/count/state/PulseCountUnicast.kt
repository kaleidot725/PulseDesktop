package jp.kaleidot725.pulse.demo.count.state

import jp.kaleidot725.pulse.demo.core.domain.PulseAreaPosition
import jp.kaleidot725.pulse.mvi.PulseUnicast

sealed class PulseCountUnicast : PulseUnicast {
    data class Pulsed(
        val origin: PulseAreaPosition,
    ) : PulseCountUnicast()
}
