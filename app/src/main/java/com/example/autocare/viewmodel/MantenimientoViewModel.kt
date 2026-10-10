package com.example.autocare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocare.data.Mantenimiento
import com.example.autocare.data.MantenimientoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MantenimientoViewModel(
    private val repository: MantenimientoRepository
) : ViewModel() {

    private val _listaMantenimientos = MutableStateFlow<List<Mantenimiento>>(emptyList())
    val listaMantenimientos: StateFlow<List<Mantenimiento>> = _listaMantenimientos.asStateFlow()

    fun cargarMantenimientosPorVehiculo(vehiculoId: Int) {
        viewModelScope.launch {
            repository.obtenerMantenimientosPorVehiculo(vehiculoId).collect { mantenimientos ->
                _listaMantenimientos.value = mantenimientos
            }
        }
    }

    fun guardarMantenimiento(
        vehiculoId: Int,
        titulo: String,
        tipo: String,
        kilometrajeRealizado: Int,
        proximoKilometraje: Int?,
        fecha: String,
        proximaFecha: String?,
        costo: Double,
        notas: String
    ) {
        viewModelScope.launch {
            val nuevoMantenimiento = Mantenimiento(
                vehiculoId = vehiculoId,
                titulo = titulo,
                tipo = tipo,
                kilometrajeRealizado = kilometrajeRealizado,
                proximoKilometraje = proximoKilometraje,
                fecha = fecha,
                proximaFecha = proximaFecha,
                costo = costo,
                notas = notas
            )
            repository.insertarMantenimiento(nuevoMantenimiento)
        }
    }

    fun eliminarMantenimiento(mantenimiento: Mantenimiento) {
        viewModelScope.launch {
            repository.eliminarMantenimiento(mantenimiento)
        }
    }
}