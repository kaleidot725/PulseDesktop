package counter.core.domain.usecase

import counter.core.common.Clock
import counter.core.domain.model.Counter
import counter.core.domain.repository.CounterRepository

class GetCounterUseCase(private val repository: CounterRepository, private val clock: Clock) {
    operator fun invoke(): Counter {
        val counter = repository.load()
        return if (clock.now() < 0) Counter(0) else counter
    }
}
