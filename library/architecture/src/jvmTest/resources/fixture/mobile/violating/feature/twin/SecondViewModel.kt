package fixture.mobile.violating.feature.twin

import jp.kaleidot725.pulse.mvi.PulseViewModel

class SecondViewModel : PulseViewModel<CounterState, CounterAction, CounterEvent>(CounterState()) {
    override fun onAction(uiAction: CounterAction) = Unit
}
