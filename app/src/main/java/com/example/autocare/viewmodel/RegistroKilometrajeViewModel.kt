
package com.example.autocare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocare.data.RegistroKilometraje
import com.example.autocare.data.RegistroKilometrajeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RegistroKilometrajeViewModel(
    private val repository: RegistroKilometrajeRepository
) : ViewModel() {

    // Consultar el historial de un vehículo
    fun obtenerHistorial(
        vehiculoId: Int
    ): Flow<List<RegistroKilometraje>> {
        return repository.obtenerRegistrosPorVehiculo(vehiculoId)
    }

    // Guardar una nueva lectura del odómetro
    fun guardarKilometraje(
        vehiculoId: Int,
        kilometrajeActual: Int
    ) {
        viewModelScope.launch {

            val nuevoRegistro = RegistroKilometraje(
                vehiculoId = vehiculoId,
                kilometrajeActual = kilometrajeActual
            )

            repository.insertarRegistro(nuevoRegistro)
        }
    }

    // Eliminar un registro del historial
    fun eliminarRegistro(
        registro: RegistroKilometraje
    ) {
        viewModelScope.launch {
            repository.eliminarRegistro(registro)
        }
    }
}
