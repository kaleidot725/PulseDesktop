package jp.kaleidot725.pulse.demo.core.domain

class ResetPulseCountUseCase(
    private val repository: PulseCountRepository,
) {
    suspend operator fun invoke(area: PulseArea) {
        repository.reset(area)
    }
}
