package jp.kaleidot725.pulse.demo.di

import jp.kaleidot725.pulse.demo.core.domain.model.PulseArea
import jp.kaleidot725.pulse.demo.count.PulseCountContainer
import jp.kaleidot725.pulse.demo.count.section.area.PulseAreaViewModel
import org.koin.dsl.module

val countFeatureModule =
    module {
        factory { (area: PulseArea) -> PulseAreaViewModel(area, get(), get()) }
        factory { (areas: List<PulseAreaViewModel>) -> PulseCountContainer(viewModels = areas) }
    }
