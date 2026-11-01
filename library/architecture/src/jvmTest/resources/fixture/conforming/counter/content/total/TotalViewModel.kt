package fixture.conforming.counter.content.total

import fixture.conforming.counter.CounterBroadcast
import fixture.conforming.counter.CounterUnicast
import jp.kaleidot725.pulse.mvi.PulseViewModel

class TotalViewModel : PulseViewModel<TotalState, TotalAction, TotalEvent, CounterBroadcast, CounterUnicast>(TotalState()) {
    override fun onAction(uiAction: TotalAction) = Unit
}
