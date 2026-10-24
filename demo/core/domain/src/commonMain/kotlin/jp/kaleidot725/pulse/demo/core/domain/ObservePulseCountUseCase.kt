package jp.kaleidot725.pulse.demo.core.domain

import kotlinx.coroutines.flow.Flow

class ObservePulseCountUseCase(
    private val repository: PulseCountRepository,
) {
    operator fun invoke(area: PulseArea): Flow<Int> = repository.count(area)
}
