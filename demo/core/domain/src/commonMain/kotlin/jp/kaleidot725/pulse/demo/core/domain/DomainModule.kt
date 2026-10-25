package jp.kaleidot725.pulse.demo.core.domain

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule =
    module {
        factoryOf(::ObservePulseCountUseCase)
        factoryOf(::SendPulseUseCase)
        factoryOf(::ReceivePulseUseCase)
        factoryOf(::ResetPulseCountUseCase)
    }
