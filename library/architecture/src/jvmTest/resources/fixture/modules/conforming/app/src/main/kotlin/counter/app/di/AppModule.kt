package counter.app.di

import counter.core.data.InMemoryCounterRepository
import counter.core.domain.CounterRepository
import org.koin.dsl.module

val appModule =
    module {
        single<CounterRepository> { InMemoryCounterRepository() }
    }
