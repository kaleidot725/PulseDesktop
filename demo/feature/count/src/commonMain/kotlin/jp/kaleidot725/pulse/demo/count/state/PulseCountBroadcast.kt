package jp.kaleidot725.pulse.demo.count.state

import jp.kaleidot725.pulse.demo.core.domain.PulseAreaPosition
import jp.kaleidot725.pulse.mvi.PulseBroadcast

sealed class PulseCountBroadcast : PulseBroadcast {
    data class Pulse(
        val origin: PulseAreaPosition,
    ) : PulseCountBroadcast()

    data object Reset : PulseCountBroadcast()
}
