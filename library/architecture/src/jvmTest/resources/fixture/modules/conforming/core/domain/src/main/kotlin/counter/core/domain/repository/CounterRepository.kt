package counter.core.domain.repository

import counter.core.domain.model.Counter

interface CounterRepository {
    fun load(): Counter
}
