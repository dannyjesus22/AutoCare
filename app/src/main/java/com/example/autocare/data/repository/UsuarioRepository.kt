package com.example.autocare.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Patterns
import com.example.autocare.data.dao.UsuarioDao
import com.example.autocare.data.entities.Usuario
import com.example.autocare.data.security.SeguridadContrasena
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

sealed class ResultadoAutenticacion {
    data class Exito(val usuario: Usuario) : ResultadoAutenticacion()
    data class Error(val mensaje: String) : ResultadoAutenticacion()
}

class UsuarioRepository @Inject constructor(
    private val usuarioDao: UsuarioDao
) {

    // Registrar una cuenta nueva.
    suspend fun registrarUsuario(
        nombre: String,
        correo: String,
        contrasena: String
    ): ResultadoAutenticacion = withContext(Dispatchers.IO) {

        val nombreLimpio = nombre.trim()
        val correoLimpio = correo.trim().lowercase(Locale.ROOT)

        when {
            nombreLimpio.isBlank() ||
                    correoLimpio.isBlank() ||
                    contrasena.isBlank() -> {
                return@withContext ResultadoAutenticacion.Error(
                    "Completa todos los campos."
                )
            }

            !Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches() -> {
                return@withContext ResultadoAutenticacion.Error(
                    "Ingresa un correo electrónico válido."
                )
            }

            contrasena.length < 8 -> {
                return@withContext ResultadoAutenticacion.Error(
                    "La contraseña debe tener al menos 8 caracteres."
                )
            }
        }

        if (usuarioDao.buscarPorCorreo(correoLimpio) != null) {
            return@withContext ResultadoAutenticacion.Error(
                "Este correo ya está registrado."
            )
        }

        val salt = SeguridadContrasena.generarSalt()
        val hash = SeguridadContrasena.generarHash(
            contrasena,
            salt
        )

        val usuario = Usuario(
            nombre = nombreLimpio,
            correo = correoLimpio,
            contrasenaHash = hash,
            salt = salt
        )

        try {
            val nuevoId = usuarioDao.insertarUsuario(usuario)

            ResultadoAutenticacion.Exito(
                usuario.copy(id = nuevoId.toInt())
            )
        } catch (e: SQLiteConstraintException) {
            ResultadoAutenticacion.Error(
                "Este correo ya está registrado."
            )
        }
    }

    // Comprobar correo y contraseña.
    suspend fun iniciarSesion(
        correo: String,
        contrasena: String
    ): ResultadoAutenticacion = withContext(Dispatchers.IO) {

        val correoLimpio = correo.trim().lowercase(Locale.ROOT)

        if (correoLimpio.isBlank() || contrasena.isBlank()) {
            return@withContext ResultadoAutenticacion.Error(
                "Ingresa tu correo y contraseña."
            )
        }

        val usuario = usuarioDao.buscarPorCorreo(correoLimpio)
            ?: return@withContext ResultadoAutenticacion.Error(
                "Correo o contraseña incorrectos."
            )

        val contrasenaCorrecta =
            SeguridadContrasena.verificarContrasena(
                contrasenaIngresada = contrasena,
                hashGuardado = usuario.contrasenaHash,
                saltGuardado = usuario.salt
            )

        if (!contrasenaCorrecta) {
            return@withContext ResultadoAutenticacion.Error(
                "Correo o contraseña incorrectos."
            )
        }

        ResultadoAutenticacion.Exito(usuario)
    }

    // Obtener los datos de un usuario.
    suspend fun obtenerUsuario(id: Int): Usuario? =
        withContext(Dispatchers.IO) {
            usuarioDao.buscarPorId(id)
        }

    // Cambiar la contraseña del usuario que inició sesión.
    suspend fun cambiarContrasena(
        usuarioId: Int,
        contrasenaActual: String,
        nuevaContrasena: String,
        confirmarContrasena: String
    ): ResultadoAutenticacion = withContext(Dispatchers.IO) {

        if (
            contrasenaActual.isBlank() ||
            nuevaContrasena.isBlank() ||
            confirmarContrasena.isBlank()
        ) {
            return@withContext ResultadoAutenticacion.Error(
                "Completa todos los campos."
            )
        }

        if (nuevaContrasena.length < 8) {
            return@withContext ResultadoAutenticacion.Error(
                "La nueva contraseña debe tener al menos 8 caracteres."
            )
        }

        if (nuevaContrasena != confirmarContrasena) {
            return@withContext ResultadoAutenticacion.Error(
                "Las nuevas contraseñas no coinciden."
            )
        }

        val usuario = usuarioDao.buscarPorId(usuarioId)
            ?: return@withContext ResultadoAutenticacion.Error(
                "No se encontró el usuario."
            )

        // Comprobar la contraseña actual.
        val contrasenaCorrecta =
            SeguridadContrasena.verificarContrasena(
                contrasenaIngresada = contrasenaActual,
                hashGuardado = usuario.contrasenaHash,
                saltGuardado = usuario.salt
            )

        if (!contrasenaCorrecta) {
            return@withContext ResultadoAutenticacion.Error(
                "La contraseña actual es incorrecta."
            )
        }

        if (contrasenaActual == nuevaContrasena) {
            return@withContext ResultadoAutenticacion.Error(
                "La nueva contraseña debe ser diferente de la actual."
            )
        }

        // Proteger la nueva contraseña.
        val nuevoSalt = SeguridadContrasena.generarSalt()
        val nuevoHash = SeguridadContrasena.generarHash(
            nuevaContrasena,
            nuevoSalt
        )

        // Actualizar la contraseña en Room.
        val filasActualizadas = usuarioDao.actualizarContrasena(
            usuarioId = usuarioId,
            nuevoHash = nuevoHash,
            nuevoSalt = nuevoSalt
        )

        if (filasActualizadas != 1) {
            return@withContext ResultadoAutenticacion.Error(
                "No se pudo actualizar la contraseña."
            )
        }

        ResultadoAutenticacion.Exito(
            usuario.copy(
                contrasenaHash = nuevoHash,
                salt = nuevoSalt
            )
        )
    }
}