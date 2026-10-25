package fixture.violating

import jp.kaleidot725.pulse.mvi.PulseAction
import jp.kaleidot725.pulse.mvi.PulseBroadcast
import jp.kaleidot725.pulse.mvi.PulseEvent
import jp.kaleidot725.pulse.mvi.PulseState
import jp.kaleidot725.pulse.mvi.PulseUnicast
import jp.kaleidot725.pulse.mvi.PulseViewModel

data class CounterState(
    val count: Int = 0,
) : PulseState

sealed class CounterAction : PulseAction

sealed class CounterEvent : PulseEvent

sealed class CounterBroadcast : PulseBroadcast

sealed class CounterUnicast : PulseUnicast

class Counter :
    PulseViewModel<CounterState, CounterAction, CounterEvent, CounterBroadcast, CounterUnicast>(CounterState()) {
    var lastCount: Int = 0

    override fun onAction(uiAction: CounterAction) = Unit
}
