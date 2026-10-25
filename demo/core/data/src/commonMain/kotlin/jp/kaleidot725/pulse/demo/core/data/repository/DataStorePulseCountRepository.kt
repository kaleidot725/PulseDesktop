package jp.kaleidot725.pulse.demo.core.data.repository

import jp.kaleidot725.pulse.demo.core.data.local.PulseCountLocalDataSource
import jp.kaleidot725.pulse.demo.core.domain.model.PulseArea
import jp.kaleidot725.pulse.demo.core.domain.repository.PulseCountRepository
import kotlinx.coroutines.flow.Flow

class DataStorePulseCountRepository(
    private val localDataSource: PulseCountLocalDataSource,
) : PulseCountRepository {
    override fun count(area: PulseArea): Flow<Int> = localDataSource.count(area.key)

    override suspend fun increment(area: PulseArea) {
        localDataSource.increment(area.key)
    }

    override suspend fun reset(area: PulseArea) {
        localDataSource.remove(area.key)
    }

    private val PulseArea.key: String
        get() = "count_${depth}_${position.name}"
}
