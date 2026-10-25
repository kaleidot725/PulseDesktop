package counter.app.di

import counter.core.data.repository.InMemoryCounterRepository
import counter.core.domain.repository.CounterRepository
import org.koin.dsl.module

val appModule =
    module {
        single<CounterRepository> { InMemoryCounterRepository() }
    }
