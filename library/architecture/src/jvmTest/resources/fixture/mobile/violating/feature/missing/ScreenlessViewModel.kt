package fixture.mobile.violating.feature.missing

import jp.kaleidot725.pulse.mvi.PulseViewModel

class ScreenlessViewModel : PulseViewModel<CounterState, CounterAction, CounterEvent>(CounterState()) {
    override fun onAction(uiAction: CounterAction) = Unit
}
