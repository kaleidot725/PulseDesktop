package fixture.mobile.violating.feature.idle

import jp.kaleidot725.pulse.mvi.PulseViewModel

class IdleViewModel : PulseViewModel<CounterState, CounterAction, CounterEvent>(CounterState()) {
    override fun onAction(uiAction: CounterAction) = Unit
}
