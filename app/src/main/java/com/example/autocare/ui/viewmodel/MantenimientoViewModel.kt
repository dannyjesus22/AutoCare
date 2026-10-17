package com.example.autocare.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocare.data.entities.Mantenimiento
import com.example.autocare.data.repository.MantenimientoRepository
import com.example.autocare.utils.NotificacionHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MantenimientoViewModel @Inject constructor(
    private val repository: MantenimientoRepository
) : ViewModel() {

    fun obtenerMantenimientosPorVehiculo(
        vehiculoId: Int
    ): Flow<List<Mantenimiento>> {
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
        notas: String,
        completado: Boolean = false
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
                notas = notas,
                completado = completado
            )

            repository.insertarMantenimiento(nuevoMantenimiento)
        }
    }

    fun cambiarEstadoMantenimiento(
        mantenimiento: Mantenimiento,
        estaCompletado: Boolean
    ) {
        viewModelScope.launch {
            val mantenimientoActualizado = mantenimiento.copy(
                completado = estaCompletado
            )

            repository.actualizarMantenimiento(mantenimientoActualizado)
        }
    }

    fun verificarNotificacionKilometraje(
        vehiculoId: Int,
        nuevoKilometraje: Int,
        context: Context
    ) {
        viewModelScope.launch {
            val mantenimientos = repository
                .obtenerMantenimientosPorVehiculo(vehiculoId)
                .firstOrNull()

            mantenimientos?.forEach { mantenimiento ->

                // Notificar solamente si el mantenimiento está pendiente.
                if (!mantenimiento.completado) {

                    val kmDisparoAlerta = mantenimiento.proximoKilometraje

                    if (
                        kmDisparoAlerta != null &&
                        nuevoKilometraje >= kmDisparoAlerta
                    ) {
                        val notificacionHelper = NotificacionHelper(context)

                        notificacionHelper.enviarNotificacionMantenimiento(
                            titulo = "Recordatorio de Mantenimiento Pendiente",
                            mensaje = "Tu vehículo alcanzó los $nuevoKilometraje km. Tienes pendiente: ${mantenimiento.titulo}"
                        )
                    }
                }
            }
        }
    }
}
