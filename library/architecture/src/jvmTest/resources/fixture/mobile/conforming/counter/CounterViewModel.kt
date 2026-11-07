package fixture.mobile.conforming.counter

import jp.kaleidot725.pulse.mvi.PulseViewModel

class CounterViewModel : PulseViewModel<CounterState, CounterAction, CounterEvent>(CounterState()) {
    override fun onAction(uiAction: CounterAction) {
        when (uiAction) {
            CounterAction.Increment -> update { copy(count = count + 1) }
        }
    }
}
