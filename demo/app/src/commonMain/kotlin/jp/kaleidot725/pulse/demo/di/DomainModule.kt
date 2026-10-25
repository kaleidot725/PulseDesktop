package jp.kaleidot725.pulse.demo.di

import jp.kaleidot725.pulse.demo.core.domain.usecase.ReceivePulseUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule =
    module {
        factoryOf(::ReceivePulseUseCase)
    }
