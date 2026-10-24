package counter.core.domain

import counter.core.common.Clock

class GetCounterUseCase(private val repository: CounterRepository, private val clock: Clock) {
    operator fun invoke(): Counter = repository.load()
}
