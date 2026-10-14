
package com.example.autocare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocare.data.RegistroKilometraje
import com.example.autocare.data.RegistroKilometrajeRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estados de la pantalla de historial.
sealed interface HistorialUiState {

    data object Loading : HistorialUiState

    data class Success(
        val registros: List<RegistroKilometraje>
    ) : HistorialUiState

    data class Error(
        val mensaje: String
    ) : HistorialUiState
}

class HistorialViewModel(
    private val repository: RegistroKilometrajeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistorialUiState>(
        HistorialUiState.Loading
    )

    val uiState: StateFlow<HistorialUiState> =
        _uiState.asStateFlow()

    private var trabajoHistorial: Job? = null

    // Cancelar el historial anterior y limpiar sus datos.
    fun limpiarHistorial() {

        trabajoHistorial?.cancel()
        trabajoHistorial = null

        _uiState.value = HistorialUiState.Success(
            registros = emptyList()
        )
    }

    // Consultar el historial comprobando el usuario propietario.
    fun cargarHistorial(
        vehiculoId: Int,
        usuarioId: Int
    ) {

        trabajoHistorial?.cancel()

        _uiState.value = HistorialUiState.Loading

        trabajoHistorial = viewModelScope.launch {

            try {

                repository.obtenerRegistrosDeUsuario(
                    vehiculoId = vehiculoId,
                    usuarioId = usuarioId
                ).collect { registros ->

                    _uiState.value = HistorialUiState.Success(
                        registros = registros
                    )
                }

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _uiState.value = HistorialUiState.Error(
                    mensaje = e.message
                        ?: "No se pudo cargar el historial."
                )
            }
        }
    }
}
