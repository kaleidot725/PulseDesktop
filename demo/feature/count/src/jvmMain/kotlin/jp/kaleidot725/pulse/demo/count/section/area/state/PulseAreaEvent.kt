package jp.kaleidot725.pulse.demo.count.section.area.state

import jp.kaleidot725.pulse.demo.core.domain.model.PulseAreaPosition
import jp.kaleidot725.pulse.mvi.PulseEvent

sealed class PulseAreaEvent : PulseEvent {
    data class Charged(
        val position: PulseAreaPosition,
        val count: Int,
    ) : PulseAreaEvent()
}
