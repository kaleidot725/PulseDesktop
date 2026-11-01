package jp.kaleidot725.pulse.demo.count.section.area.state

import jp.kaleidot725.pulse.mvi.PulseAction

sealed class PulseAreaAction : PulseAction {
    data object Pulse : PulseAreaAction()
}
