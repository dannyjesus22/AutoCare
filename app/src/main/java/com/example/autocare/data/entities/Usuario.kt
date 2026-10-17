package com.example.autocare.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Representa una cuenta de usuario almacenada en Room.
@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["correo"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,

    val correo: String,

    // Nunca guardaremos la contraseña original.
    val contrasenaHash: String,

    // Valor aleatorio utilizado para proteger la contraseña.
    val salt: String
)