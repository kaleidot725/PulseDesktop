package jp.kaleidot725.pulse.demo.core.domain

class SendPulseUseCase(
    private val repository: PulseCountRepository,
) {
    suspend operator fun invoke(area: PulseArea) {
        repository.increment(area)
    }
}
