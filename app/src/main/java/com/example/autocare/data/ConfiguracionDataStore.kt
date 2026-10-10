package com.example.autocare.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "configuracion_preferences")

class ConfiguracionDataStore(private val context: Context) {

    companion object {
        val NOTIFICACIONES_ACTIVAS = booleanPreferencesKey("notificaciones_activas")
        val RECORDATORIOS_ACTIVOS = booleanPreferencesKey("recordatorios_activos")
    }

    val notificacionesActivadas: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICACIONES_ACTIVAS] ?: true
    }

    val recordatoriosActivados: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[RECORDATORIOS_ACTIVOS] ?: true
    }

    suspend fun guardarNotificacionesActivadas(activadas: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICACIONES_ACTIVAS] = activadas
        }
    }

    suspend fun guardarRecordatoriosActivados(activados: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[RECORDATORIOS_ACTIVOS] = activados
        }
    }
}