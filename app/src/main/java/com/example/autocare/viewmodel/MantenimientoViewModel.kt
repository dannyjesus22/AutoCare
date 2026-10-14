package com.example.autocare.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocare.data.Mantenimiento
import com.example.autocare.data.MantenimientoRepository
import com.example.autocare.utils.NotificacionHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MantenimientoViewModel(
    private val repository: MantenimientoRepository
) : ViewModel() {

    fun obtenerMantenimientosPorVehiculo(vehiculoId: Int): Flow<List<Mantenimiento>> {
        return repository.obtenerMantenimientosPorVehiculo(vehiculoId)
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

    fun verificarNotificacionKilometraje(
        vehiculoId: Int,
        nuevoKilometraje: Int,
        context: Context
    ) {
        viewModelScope.launch {
            val mantenimientos = repository.obtenerMantenimientosPorVehiculo(vehiculoId).firstOrNull()

            mantenimientos?.forEach { mantenimiento ->
                val kmDisparoAlerta = mantenimiento.proximoKilometraje
                if (kmDisparoAlerta != null && nuevoKilometraje >= kmDisparoAlerta) {
                    val notificacionHelper = NotificacionHelper(context)
                    notificacionHelper.enviarNotificacionMantenimiento(
                        titulo = "Recordatorio de Mantenimiento",
                        mensaje = "Tu vehículo alcanzó los $nuevoKilometraje km. Tienes pendiente: ${mantenimiento.titulo}"
                    )
                }
            }
        }
    }
}