package jp.kaleidot725.pulse.demo.core.domain.usecase

import jp.kaleidot725.pulse.demo.core.domain.model.PulseArea
import jp.kaleidot725.pulse.demo.core.domain.model.PulseAreaPosition
import jp.kaleidot725.pulse.demo.core.domain.repository.PulseCountRepository

/** An area counts a pulse from a neighbour, and ignores its own and one from across the grid. */
class ReceivePulseUseCase(
    private val repository: PulseCountRepository,
) {
    suspend operator fun invoke(
        area: PulseArea,
        origin: PulseAreaPosition,
    ) {
        if (origin == area.position) return
        if (origin !in area.position.neighbors) return

        repository.increment(area)
    }
}
