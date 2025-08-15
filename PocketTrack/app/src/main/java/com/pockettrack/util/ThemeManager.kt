package com.pockettrack.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

object ThemeManager {
    private val KEY_DARK = booleanPreferencesKey("dark_mode")

    fun isDarkFlow(context: Context): Flow<Boolean> = context.dataStore.data.map { it[KEY_DARK] ?: false }

    suspend fun setDark(context: Context, dark: Boolean) {
        context.dataStore.edit { it[KEY_DARK] = dark }
        AppCompatDelegate.setDefaultNightMode(if (dark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }
}