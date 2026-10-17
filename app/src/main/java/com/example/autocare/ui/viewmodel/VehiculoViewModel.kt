package com.example.autocare.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocare.data.entities.Vehiculo
import com.example.autocare.data.repository.VehiculoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehiculoViewModel @Inject constructor(
    private val repository: VehiculoRepository
) : ViewModel() {

    // ID del usuario que inició sesión.
    private val usuarioActivoId = MutableStateFlow<Int?>(null)

    // Mostrar solamente los vehículos del usuario activo.
    val vehiculos = usuarioActivoId
        .flatMapLatest { usuarioId ->
            if (usuarioId != null) {
                repository.obtenerVehiculosPorUsuario(usuarioId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Establecer el usuario que está utilizando AutoCare.
    fun establecerUsuarioActivo(usuarioId: Int?) {
        usuarioActivoId.value = usuarioId
    }

    // Guardar un vehículo asociado a su propietario.
    fun guardarVehiculo(vehiculo: Vehiculo) {
        val usuarioId = usuarioActivoId.value ?: return

        viewModelScope.launch {
            repository.insertarVehiculo(
                vehiculo.copy(
                    id = 0,
                    usuarioId = usuarioId
                )
            )
        }
    }

    // Actualizar únicamente vehículos propios.
    fun actualizarVehiculo(vehiculo: Vehiculo) {
        val usuarioId = usuarioActivoId.value ?: return

        viewModelScope.launch {
            val vehiculoPropio = repository.obtenerVehiculoDeUsuario(
                vehiculo.id,
                usuarioId
            )

            if (vehiculoPropio != null) {
                repository.actualizarVehiculo(
                    vehiculo.copy(usuarioId = usuarioId)
                )
            }
        }
    }

    // Eliminar únicamente vehículos propios.
    fun eliminarVehiculo(vehiculo: Vehiculo) {
        val usuarioId = usuarioActivoId.value ?: return

        viewModelScope.launch {
            val vehiculoPropio = repository.obtenerVehiculoDeUsuario(
                vehiculo.id,
                usuarioId
            )

            if (vehiculoPropio != null) {
                repository.eliminarVehiculo(vehiculoPropio)
            }
        }
    }

    // Buscar únicamente un vehículo del usuario activo.
    suspend fun obtenerVehiculoPorId(id: Int): Vehiculo? {
        val usuarioId = usuarioActivoId.value ?: return null

        return repository.obtenerVehiculoDeUsuario(
            id,
            usuarioId
        )
    }
}
