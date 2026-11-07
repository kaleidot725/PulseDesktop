package fixture.mobile.violating.feature.stray

import jp.kaleidot725.pulse.mvi.PulseViewModel

class StrayViewModel : PulseViewModel<CounterState, CounterAction, CounterEvent>(CounterState()) {
    override fun onAction(uiAction: CounterAction) = Unit
}
