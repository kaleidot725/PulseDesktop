package jp.kaleidot725.pulse.demo.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import jp.kaleidot725.pulse.demo.core.domain.PulseArea
import jp.kaleidot725.pulse.demo.core.domain.PulseCountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath

class DataStorePulseCountRepository(
    private val dataStore: DataStore<Preferences>,
) : PulseCountRepository {
    override fun count(area: PulseArea): Flow<Int> =
        dataStore.data
            .map { preferences -> preferences[area.key] ?: 0 }
            .distinctUntilChanged()

    override suspend fun increment(area: PulseArea) {
        dataStore.edit { preferences -> preferences[area.key] = (preferences[area.key] ?: 0) + 1 }
    }

    override suspend fun reset(area: PulseArea) {
        dataStore.edit { preferences -> preferences.remove(area.key) }
    }

    private val PulseArea.key: Preferences.Key<Int>
        get() = intPreferencesKey("count_${depth}_${position.name}")

    companion object {
        fun create(path: String): DataStorePulseCountRepository =
            DataStorePulseCountRepository(PreferenceDataStoreFactory.createWithPath { path.toPath() })
    }
}
