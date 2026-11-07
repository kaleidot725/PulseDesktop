package fixture.mobile.conforming.counter

import jp.kaleidot725.pulse.mvi.PulseAction
import jp.kaleidot725.pulse.mvi.PulseEvent
import jp.kaleidot725.pulse.mvi.PulseState

data class CounterState(
    val count: Int = 0,
) : PulseState

sealed class CounterAction : PulseAction {
    data object Increment : CounterAction()
}

sealed class CounterEvent : PulseEvent {
    data object Back : CounterEvent()
}
