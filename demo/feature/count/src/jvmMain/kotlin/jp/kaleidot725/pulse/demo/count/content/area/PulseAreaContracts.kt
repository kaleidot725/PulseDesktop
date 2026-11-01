package jp.kaleidot725.pulse.demo.count.content.area

import jp.kaleidot725.pulse.demo.core.domain.model.PulseAreaPosition
import jp.kaleidot725.pulse.mvi.PulseAction
import jp.kaleidot725.pulse.mvi.PulseEvent
import jp.kaleidot725.pulse.mvi.PulseState

data class PulseAreaState(
    val position: PulseAreaPosition = PulseAreaPosition.TopLeft,
    val count: Int = 0,
    val setupCount: Int = 0,
) : PulseState

sealed class PulseAreaAction : PulseAction {
    data object Pulse : PulseAreaAction()
}

sealed class PulseAreaEvent : PulseEvent {
    data class Charged(
        val position: PulseAreaPosition,
        val count: Int,
    ) : PulseAreaEvent()
}
