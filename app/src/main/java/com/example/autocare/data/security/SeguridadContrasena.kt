package com.example.autocare.data.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object SeguridadContrasena {

    private const val ITERACIONES = 210000
    private const val LONGITUD_CLAVE = 256

    // Genera una sal aleatoria para cada cuenta.
    fun generarSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)

        return Base64.encodeToString(
            bytes,
            Base64.NO_WRAP
        )
    }

    // Convierte la contraseña en un hash seguro.
    fun generarHash(
        contrasena: String,
        salt: String
    ): String {

        val saltBytes = Base64.decode(
            salt,
            Base64.NO_WRAP
        )

        val especificacion = PBEKeySpec(
            contrasena.toCharArray(),
            saltBytes,
            ITERACIONES,
            LONGITUD_CLAVE
        )

        return try {
            val fabrica = SecretKeyFactory.getInstance(
                "PBKDF2WithHmacSHA256"
            )

            val hashBytes = fabrica
                .generateSecret(especificacion)
                .encoded

            Base64.encodeToString(
                hashBytes,
                Base64.NO_WRAP
            )
        } finally {
            especificacion.clearPassword()
        }
    }

    // Comprueba si la contraseña ingresada es correcta.
    fun verificarContrasena(
        contrasenaIngresada: String,
        hashGuardado: String,
        saltGuardado: String
    ): Boolean {

        val hashIngresado = generarHash(
            contrasenaIngresada,
            saltGuardado
        )

        return MessageDigest.isEqual(
            Base64.decode(hashIngresado, Base64.NO_WRAP),
            Base64.decode(hashGuardado, Base64.NO_WRAP)
        )
    }
}