package jp.kaleidot725.pulse.demo.di

import jp.kaleidot725.pulse.demo.core.data.local.PulseCountLocalDataSource
import jp.kaleidot725.pulse.demo.core.data.local.pulseCountDataStore
import jp.kaleidot725.pulse.demo.core.data.repository.DataStorePulseCountRepository
import jp.kaleidot725.pulse.demo.core.domain.repository.PulseCountRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun dataModule(countsFile: String): Module =
    module {
        single { pulseCountDataStore(countsFile) }
        singleOf(::PulseCountLocalDataSource)
        singleOf(::DataStorePulseCountRepository) { bind<PulseCountRepository>() }
    }
