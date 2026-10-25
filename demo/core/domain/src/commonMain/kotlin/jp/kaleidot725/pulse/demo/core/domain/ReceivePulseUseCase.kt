package jp.kaleidot725.pulse.demo.core.domain

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
