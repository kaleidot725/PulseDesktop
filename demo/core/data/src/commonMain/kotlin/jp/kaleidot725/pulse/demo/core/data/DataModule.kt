package jp.kaleidot725.pulse.demo.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import jp.kaleidot725.pulse.demo.core.domain.PulseCountRepository
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun dataModule(countsFile: String): Module =
    module {
        single<DataStore<Preferences>> { PreferenceDataStoreFactory.createWithPath { countsFile.toPath() } }
        singleOf(::DataStorePulseCountRepository) { bind<PulseCountRepository>() }
    }
