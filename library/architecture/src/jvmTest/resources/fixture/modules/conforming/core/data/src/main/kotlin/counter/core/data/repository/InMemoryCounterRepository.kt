package counter.core.data.repository

import counter.core.data.local.CounterDataSource
import counter.core.domain.model.Counter
import counter.core.domain.repository.CounterRepository

class InMemoryCounterRepository(private val source: CounterDataSource = CounterDataSource()) : CounterRepository {
    override fun load(): Counter = Counter(source.read().value)
}
