package jp.kaleidot725.pulse.demo.core.data.repository

import jp.kaleidot725.pulse.demo.core.data.local.PulseCountLocalDataSource
import jp.kaleidot725.pulse.demo.core.domain.model.PulseArea
import jp.kaleidot725.pulse.demo.core.domain.repository.PulseCountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DataStorePulseCountRepository(
    private val localDataSource: PulseCountLocalDataSource,
) : PulseCountRepository {
    // An increment reads and then writes, so two at once — an area's own tap and a pulse from its
    // neighbour — take turns instead of both writing the same count plus one.
    private val writing = Mutex()

    override fun count(area: PulseArea): Flow<Int> = localDataSource.read(area.key).map { it ?: 0 }

    override suspend fun increment(area: PulseArea) {
        writing.withLock {
            val current = localDataSource.read(area.key).first() ?: 0
            localDataSource.write(area.key, current + 1)
        }
    }

    override suspend fun reset(area: PulseArea) {
        localDataSource.remove(area.key)
    }

    private val PulseArea.key: String
        get() = "count_${depth}_${position.name}"
}
