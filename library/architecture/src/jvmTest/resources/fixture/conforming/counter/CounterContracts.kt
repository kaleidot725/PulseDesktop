package fixture.conforming.counter

import jp.kaleidot725.pulse.mvi.PulseBroadcast
import jp.kaleidot725.pulse.mvi.PulseUnicast

sealed class CounterBroadcast : PulseBroadcast {
    data object Reset : CounterBroadcast()
}

sealed class CounterUnicast : PulseUnicast

data object Cleared : PulseBroadcast
