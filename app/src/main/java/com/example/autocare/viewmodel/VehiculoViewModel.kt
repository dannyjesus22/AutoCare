
package com.example.autocare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocare.data.Vehiculo
import com.example.autocare.data.VehiculoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VehiculoViewModel(
    private val repository: VehiculoRepository
) : ViewModel() {

    // Lista de vehículos registrados
    val vehiculos = repository.todosLosVehiculos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Guardar un vehículo
    fun guardarVehiculo(vehiculo: Vehiculo) {
        viewModelScope.launch {
            repository.insertarVehiculo(vehiculo)
        }
    }

    // Actualizar un vehículo
    fun actualizarVehiculo(vehiculo: Vehiculo) {
        viewModelScope.launch {
            repository.actualizarVehiculo(vehiculo)
        }
    }

    // Eliminar un vehículo
    fun eliminarVehiculo(vehiculo: Vehiculo) {
        viewModelScope.launch {
            repository.eliminarVehiculo(vehiculo)
        }
    }
}
