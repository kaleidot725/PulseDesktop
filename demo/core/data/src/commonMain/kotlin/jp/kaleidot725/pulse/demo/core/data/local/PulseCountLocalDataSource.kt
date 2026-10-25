package jp.kaleidot725.pulse.demo.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/** The counts as DataStore keeps them, one integer per key; what a key means is the repository's business. */
class PulseCountLocalDataSource(
    private val dataStore: DataStore<Preferences>,
) {
    fun count(key: String): Flow<Int> =
        dataStore.data
            .map { preferences -> preferences[intPreferencesKey(key)] ?: 0 }
            .distinctUntilChanged()

    suspend fun increment(key: String) {
        dataStore.edit { preferences ->
            val counter = intPreferencesKey(key)
            preferences[counter] = (preferences[counter] ?: 0) + 1
        }
    }

    suspend fun remove(key: String) {
        dataStore.edit { preferences -> preferences.remove(intPreferencesKey(key)) }
    }
}
