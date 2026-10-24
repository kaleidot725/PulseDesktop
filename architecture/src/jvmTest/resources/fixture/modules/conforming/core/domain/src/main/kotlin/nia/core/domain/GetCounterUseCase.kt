package nia.core.domain

import nia.core.common.Clock
import nia.core.model.Counter

class GetCounterUseCase(private val repository: CounterRepository, private val clock: Clock) {
    operator fun invoke(): Counter = repository.load()
}
