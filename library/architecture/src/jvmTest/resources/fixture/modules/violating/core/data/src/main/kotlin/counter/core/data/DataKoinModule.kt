package counter.core.data

import counter.core.data.repository.InMemoryCounterRepository
import counter.core.domain.repository.CounterRepository
import org.koin.dsl.module

val dataKoinModule =
    module {
        single<CounterRepository> { InMemoryCounterRepository() }
    }
