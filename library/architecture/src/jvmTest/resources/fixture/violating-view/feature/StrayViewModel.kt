package fixture.violating.feature

import jp.kaleidot725.pulse.mvi.PulseViewModel

class StrayViewModel : PulseViewModel<StrayState, StrayAction, StrayEvent, StrayBroadcast, StrayUnicast>(StrayState) {
    override fun onAction(uiAction: StrayAction) = Unit
}
