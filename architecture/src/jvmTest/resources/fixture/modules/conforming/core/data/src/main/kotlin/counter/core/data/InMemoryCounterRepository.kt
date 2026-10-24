package counter.core.data

import counter.core.domain.Counter
import counter.core.domain.CounterRepository

class InMemoryCounterRepository : CounterRepository {
    override fun load(): Counter = Counter(0)
}
