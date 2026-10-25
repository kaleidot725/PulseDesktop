package fixture.conforming.state

import jp.kaleidot725.pulse.mvi.PulseAction
import jp.kaleidot725.pulse.mvi.PulseBroadcast
import jp.kaleidot725.pulse.mvi.PulseEvent
import jp.kaleidot725.pulse.mvi.PulseState
import jp.kaleidot725.pulse.mvi.PulseUnicast

data class CounterState(
    val count: Int = 0,
) : PulseState

sealed class CounterAction : PulseAction {
    data object Increment : CounterAction()

    data class Add(
        val amount: Int,
    ) : CounterAction()

    sealed class Edit : CounterAction() {
        data object Undo : Edit()
    }
}

sealed class CounterEvent : PulseEvent {
    data class Ping(
        val text: String,
    ) : CounterEvent()
}

sealed class CounterBroadcast : PulseBroadcast {
    data object Reset : CounterBroadcast()
}

sealed class CounterUnicast : PulseUnicast

data class Pinged(
    val text: String,
) : PulseEvent

data object Cleared : PulseBroadcast

data object EmptyState : PulseState
