
package com.example.autocare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocare.data.RegistroKilometraje
import com.example.autocare.data.IRegistroKilometrajeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Representa los posibles estados del historial.
sealed interface HistorialUiState {

    // Todavía estamos consultando los datos.
    data object Loading : HistorialUiState

    // Los datos se cargaron correctamente.
    data class Success(
        val registros: List<RegistroKilometraje>
    ) : HistorialUiState

    // Ocurrió un error al consultar los datos.
    data class Error(
        val mensaje: String
    ) : HistorialUiState
}

class HistorialViewModel(
    private val repository: IRegistroKilometrajeRepository
) : ViewModel() {

    // Estado interno que el ViewModel puede modificar.
    private val _uiState =
        MutableStateFlow<HistorialUiState>(
            HistorialUiState.Loading
        )

    // Estado público que la pantalla puede observar.
    val uiState: StateFlow<HistorialUiState> =
        _uiState.asStateFlow()

    // Guarda la consulta activa del historial.
    private var trabajoHistorial: Job? = null

    // Consulta el historial del vehículo seleccionado.
    fun cargarHistorial(vehiculoId: Int) {

        // Cancelamos la consulta anterior si el usuario
        // selecciona otro vehículo.
        trabajoHistorial?.cancel()

        trabajoHistorial = viewModelScope.launch {

            // Mostramos el estado de carga.
            _uiState.value = HistorialUiState.Loading

            try {

                // Room entrega los registros mediante Flow.
                repository.obtenerRegistrosPorVehiculo(
                    vehiculoId
                ).collect { registros ->

                    // Cada vez que Room emite nuevos datos,
                    // actualizamos el estado de la pantalla.
                    _uiState.value = HistorialUiState.Success(
                        registros = registros
                    )
                }

            } catch (e: kotlinx.coroutines.CancellationException) {

                // Una cancelación normal no es un error.
                throw e

            } catch (e: Exception) {

                // Si ocurre un error, lo comunicamos a la UI.
                _uiState.value = HistorialUiState.Error(
                    mensaje = e.message
                        ?: "No se pudo cargar el historial."
                )
            }
        }
    }
}
