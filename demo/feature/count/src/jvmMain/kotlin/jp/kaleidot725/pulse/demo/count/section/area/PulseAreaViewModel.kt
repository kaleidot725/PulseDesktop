package jp.kaleidot725.pulse.demo.count.section.area

import jp.kaleidot725.pulse.demo.core.domain.model.PulseArea
import jp.kaleidot725.pulse.demo.core.domain.model.PulseAreaPosition
import jp.kaleidot725.pulse.demo.core.domain.repository.PulseCountRepository
import jp.kaleidot725.pulse.demo.core.domain.usecase.ReceivePulseUseCase
import jp.kaleidot725.pulse.demo.count.section.area.state.PulseAreaAction
import jp.kaleidot725.pulse.demo.count.section.area.state.PulseAreaEvent
import jp.kaleidot725.pulse.demo.count.section.area.state.PulseAreaState
import jp.kaleidot725.pulse.demo.count.state.PulseCountBroadcast
import jp.kaleidot725.pulse.demo.count.state.PulseCountUnicast
import jp.kaleidot725.pulse.mvi.PulseViewModel
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.launch

class PulseAreaViewModel(
    private val area: PulseArea,
    private val repository: PulseCountRepository,
    private val receivePulse: ReceivePulseUseCase,
) : PulseViewModel<PulseAreaState, PulseAreaAction, PulseAreaEvent, PulseCountBroadcast, PulseCountUnicast>(
        initialUiState = PulseAreaState(position = area.position),
    ) {
    override fun onSetup() {
        update { copy(setupCount = setupCount + 1) }
        coroutineScope.launch {
            repository.count(area).withIndex().collect { (index, count) -> showCount(count, isFirst = index == 0) }
        }
    }

    override fun onAction(uiAction: PulseAreaAction) {
        when (uiAction) {
            PulseAreaAction.Pulse -> pulse()
        }
    }

    override fun onReceive(broadcast: PulseCountBroadcast) {
        when (broadcast) {
            is PulseCountBroadcast.Pulse -> receive(broadcast.origin)
            PulseCountBroadcast.Reset -> reset()
        }
    }

    private fun pulse() {
        coroutineScope.launch {
            repository.increment(area)
            unicast(PulseCountUnicast.Pulsed(area.position))
        }
    }

    private fun receive(origin: PulseAreaPosition) {
        coroutineScope.launch { receivePulse(area, origin) }
    }

    private fun reset() {
        coroutineScope.launch { repository.reset(area) }
    }

    private fun showCount(
        count: Int,
        isFirst: Boolean,
    ) {
        val before = currentState.count
        update { copy(count = count) }

        if (!isFirst && before < CHARGED_AT && count >= CHARGED_AT) {
            event(PulseAreaEvent.Charged(area.position, count))
        }
    }
}

private const val CHARGED_AT = 12
