
package com.example.autocare.viewmodel.usuarios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocare.data.usuarios.ResultadoAutenticacion
import com.example.autocare.data.usuarios.Usuario
import com.example.autocare.data.usuarios.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estados que puede mostrar el registro o inicio de sesión.
sealed interface EstadoUsuario {
    data object Inicial : EstadoUsuario
    data object Cargando : EstadoUsuario
    data class Exito(val usuario: Usuario) : EstadoUsuario
    data class Error(val mensaje: String) : EstadoUsuario
}

class UsuarioViewModel(
    private val repository: UsuarioRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<EstadoUsuario>(
        EstadoUsuario.Inicial
    )

    val estado: StateFlow<EstadoUsuario> = _estado.asStateFlow()

    // Crear una cuenta nueva.
    fun registrar(
        nombre: String,
        correo: String,
        contrasena: String,
        confirmarContrasena: String
    ) {
        if (_estado.value is EstadoUsuario.Cargando) return

        if (contrasena != confirmarContrasena) {
            _estado.value = EstadoUsuario.Error(
                "Las contraseñas no coinciden."
            )
            return
        }

        viewModelScope.launch {
            _estado.value = EstadoUsuario.Cargando

            try {
                val resultado = repository.registrarUsuario(
                    nombre,
                    correo,
                    contrasena
                )

                actualizarEstado(resultado)
            } catch (e: Exception) {
                _estado.value = EstadoUsuario.Error(
                    "No se pudo crear la cuenta. Inténtalo nuevamente."
                )
            }
        }
    }

    // Iniciar sesión con una cuenta existente.
    fun iniciarSesion(
        correo: String,
        contrasena: String
    ) {
        if (_estado.value is EstadoUsuario.Cargando) return

        viewModelScope.launch {
            _estado.value = EstadoUsuario.Cargando

            try {
                val resultado = repository.iniciarSesion(
                    correo,
                    contrasena
                )

                actualizarEstado(resultado)
            } catch (e: Exception) {
                _estado.value = EstadoUsuario.Error(
                    "No se pudo iniciar sesión. Inténtalo nuevamente."
                )
            }
        }
    }

    // Convertir la respuesta del repositorio en un estado de pantalla.
    private fun actualizarEstado(
        resultado: ResultadoAutenticacion
    ) {
        _estado.value = when (resultado) {
            is ResultadoAutenticacion.Exito ->
                EstadoUsuario.Exito(resultado.usuario)

            is ResultadoAutenticacion.Error ->
                EstadoUsuario.Error(resultado.mensaje)
        }
    }

    // Limpiar mensajes anteriores al cambiar de pantalla.
    fun limpiarEstado() {
        _estado.value = EstadoUsuario.Inicial
    }
}
