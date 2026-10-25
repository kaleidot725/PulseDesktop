package jp.kaleidot725.pulse.demo.di

import jp.kaleidot725.pulse.demo.core.domain.ObservePulseCountUseCase
import jp.kaleidot725.pulse.demo.core.domain.ReceivePulseUseCase
import jp.kaleidot725.pulse.demo.core.domain.ResetPulseCountUseCase
import jp.kaleidot725.pulse.demo.core.domain.SendPulseUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule =
    module {
        factoryOf(::ObservePulseCountUseCase)
        factoryOf(::SendPulseUseCase)
        factoryOf(::ReceivePulseUseCase)
        factoryOf(::ResetPulseCountUseCase)
    }
