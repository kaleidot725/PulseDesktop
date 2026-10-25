package counter.core.data

import counter.core.domain.CounterRepository
import org.koin.dsl.module

val dataKoinModule =
    module {
        single<CounterRepository> { InMemoryCounterRepository() }
    }
