package jp.kaleidot725.pulse.demo.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

fun pulseCountDataStore(countsFile: String): DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath { countsFile.toPath() }
