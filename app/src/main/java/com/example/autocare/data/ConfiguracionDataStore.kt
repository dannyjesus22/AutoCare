
package com.example.autocare.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Un solo DataStore para las preferencias de AutoCare.
private val Context.dataStore: DataStore<Preferences> by
preferencesDataStore(name = "configuracion_preferences")

class ConfiguracionDataStore(private val context: Context) {

    companion object {

        // Configuraciones que ya existían.
        val NOTIFICACIONES_ACTIVAS =
            booleanPreferencesKey("notificaciones_activas")

        val RECORDATORIOS_ACTIVOS =
            booleanPreferencesKey("recordatorios_activos")

        // NUEVO: identificador del usuario que inició sesión.
        val USUARIO_ACTIVO_ID =
            intPreferencesKey("usuario_activo_id")
    }

    // Consultar si las notificaciones están activadas.
    val notificacionesActivadas: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[NOTIFICACIONES_ACTIVAS] ?: true
        }

    // Consultar si los recordatorios están activados.
    val recordatoriosActivados: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[RECORDATORIOS_ACTIVOS] ?: true
        }

    // NUEVO: obtener el ID del usuario que inició sesión.
    // null significa que no hay ninguna sesión iniciada.
    val usuarioActivoId: Flow<Int?> =
        context.dataStore.data.map { preferences ->
            preferences[USUARIO_ACTIVO_ID]
        }

    // Guardar la configuración de notificaciones.
    suspend fun guardarNotificacionesActivadas(
        activadas: Boolean
    ) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICACIONES_ACTIVAS] = activadas
        }
    }

    // Guardar la configuración de recordatorios.
    suspend fun guardarRecordatoriosActivados(
        activados: Boolean
    ) {
        context.dataStore.edit { preferences ->
            preferences[RECORDATORIOS_ACTIVOS] = activados
        }
    }

    // NUEVO: guardar el usuario que inició sesión.
    suspend fun guardarUsuarioActivo(usuarioId: Int) {
        require(usuarioId > 0) {
            "El ID del usuario debe ser válido."
        }

        context.dataStore.edit { preferences ->
            preferences[USUARIO_ACTIVO_ID] = usuarioId
        }
    }

    // NUEVO: cerrar sesión sin borrar vehículos ni registros.
    suspend fun cerrarSesion() {
        context.dataStore.edit { preferences ->
            preferences.remove(USUARIO_ACTIVO_ID)
        }
    }
}
