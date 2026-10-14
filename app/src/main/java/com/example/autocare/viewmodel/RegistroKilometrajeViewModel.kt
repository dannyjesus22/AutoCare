
package com.example.autocare.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocare.data.RegistroKilometraje
import com.example.autocare.data.RegistroKilometrajeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class RegistroKilometrajeViewModel(
    private val repository: RegistroKilometrajeRepository
) : ViewModel() {

    // Usuario que tiene la sesión iniciada.
    private var usuarioActivoId: Int? = null

    fun establecerUsuarioActivo(usuarioId: Int?) {
        usuarioActivoId = usuarioId
    }

    // Consultar solamente el historial de un vehículo propio.
    // Esta función es suspend porque primero verifica el propietario.
    suspend fun obtenerHistorial(
        vehiculoId: Int
    ): Flow<List<RegistroKilometraje>> {

        val usuarioId = usuarioActivoId
            ?: return flowOf(emptyList())

        return repository.obtenerRegistrosDeUsuario(
            vehiculoId = vehiculoId,
            usuarioId = usuarioId
        )
    }

    // Guardar kilometraje solamente en vehículos propios.
    fun guardarKilometraje(
        vehiculoId: Int,
        kilometrajeActual: Int
    ) {

        val usuarioId = usuarioActivoId ?: return

        viewModelScope.launch {
            try {

                repository.guardarKilometrajeDeUsuario(
                    vehiculoId = vehiculoId,
                    kilometrajeActual = kilometrajeActual,
                    usuarioId = usuarioId
                )

            } catch (e: Exception) {

                Log.e(
                    "AutoCareKilometraje",
                    "Error al guardar kilometraje",
                    e
                )
            }
        }
    }

    // Eliminar solamente registros de vehículos propios.
    fun eliminarRegistro(
        registro: RegistroKilometraje
    ) {

        val usuarioId = usuarioActivoId ?: return

        viewModelScope.launch {
            try {

                repository.eliminarRegistroDeUsuario(
                    registro = registro,
                    usuarioId = usuarioId
                )

            } catch (e: Exception) {

                Log.e(
                    "AutoCareKilometraje",
                    "Error al eliminar registro",
                    e
                )
            }
        }
    }
}
