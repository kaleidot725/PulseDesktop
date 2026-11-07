package fixture.mobile.violating.feature.unbound

import jp.kaleidot725.pulse.mvi.PulseViewModel

class UnboundViewModel : PulseViewModel<CounterState, CounterAction, CounterEvent>(CounterState()) {
    override fun onAction(uiAction: CounterAction) = Unit
}
