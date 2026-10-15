
package com.example.autocare.data.usuarios

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UsuarioDao {

    // Guarda una nueva cuenta en Room.
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarUsuario(usuario: Usuario): Long

    // Busca una cuenta mediante su correo electrónico.
    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): Usuario?

    // Busca una cuenta mediante su ID.
    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): Usuario?

    // Cuenta cuántos usuarios existen en Room.
    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contarUsuarios(): Int

    // Actualiza únicamente el hash y el salt del usuario indicado.
    @Query(
        """
        UPDATE usuarios
        SET contrasenaHash = :nuevoHash,
            salt = :nuevoSalt
        WHERE id = :usuarioId
        """
    )
    suspend fun actualizarContrasena(
        usuarioId: Int,
        nuevoHash: String,
        nuevoSalt: String
    ): Int
}
