package fixture.conforming

import fixture.conforming.state.CounterAction
import fixture.conforming.state.CounterBroadcast
import fixture.conforming.state.CounterEvent
import fixture.conforming.state.CounterState
import fixture.conforming.state.CounterUnicast
import jp.kaleidot725.pulse.mvi.PulseContainer
import jp.kaleidot725.pulse.mvi.PulseViewModel

class CounterViewModel :
    PulseViewModel<CounterState, CounterAction, CounterEvent, CounterBroadcast, CounterUnicast>(CounterState()) {
    override fun onAction(uiAction: CounterAction) = Unit
}

class CounterContainer(
    viewModels: List<PulseViewModel<*, *, *, CounterBroadcast, CounterUnicast>>,
) : PulseContainer<CounterBroadcast, CounterUnicast>(viewModels)
