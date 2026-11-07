package fixture.mobile.violating.feature.nested.content.inner

import jp.kaleidot725.pulse.mvi.PulseViewModel

class NestedViewModel : PulseViewModel<CounterState, CounterAction, CounterEvent>(CounterState()) {
    override fun onAction(uiAction: CounterAction) = Unit
}
