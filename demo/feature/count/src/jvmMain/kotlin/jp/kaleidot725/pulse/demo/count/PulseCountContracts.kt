package jp.kaleidot725.pulse.demo.count

import jp.kaleidot725.pulse.demo.core.domain.model.PulseAreaPosition
import jp.kaleidot725.pulse.mvi.PulseBroadcast
import jp.kaleidot725.pulse.mvi.PulseUnicast

sealed class PulseCountBroadcast : PulseBroadcast {
    data class Pulse(
        val origin: PulseAreaPosition,
    ) : PulseCountBroadcast()

    data object Reset : PulseCountBroadcast()
}

sealed class PulseCountUnicast : PulseUnicast {
    data class Pulsed(
        val origin: PulseAreaPosition,
    ) : PulseCountUnicast()
}
