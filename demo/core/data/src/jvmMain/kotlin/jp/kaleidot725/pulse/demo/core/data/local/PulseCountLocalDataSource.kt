package jp.kaleidot725.pulse.demo.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Stores the counts in DataStore, one integer per key. It only reads, writes and removes what it is given: what a key
 * means, and how a count changes, is the repository's business.
 */
class PulseCountLocalDataSource(
    private val dataStore: DataStore<Preferences>,
) {
    /** The stored value, or null when nothing is stored under [key]. */
    fun read(key: String): Flow<Int?> =
        dataStore.data
            .map { preferences -> preferences[intPreferencesKey(key)] }
            .distinctUntilChanged()

    suspend fun write(
        key: String,
        value: Int,
    ) {
        dataStore.edit { preferences -> preferences[intPreferencesKey(key)] = value }
    }

    suspend fun remove(key: String) {
        dataStore.edit { preferences -> preferences.remove(intPreferencesKey(key)) }
    }
}
