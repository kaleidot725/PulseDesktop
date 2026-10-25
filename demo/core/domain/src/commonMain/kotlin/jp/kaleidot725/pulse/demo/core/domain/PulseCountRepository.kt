package jp.kaleidot725.pulse.demo.core.domain

import kotlinx.coroutines.flow.Flow

interface PulseCountRepository {
    fun count(area: PulseArea): Flow<Int>

    suspend fun increment(area: PulseArea)

    suspend fun reset(area: PulseArea)
}
