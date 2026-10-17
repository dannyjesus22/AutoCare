package com.example.autocare.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehiculos")
data class Vehiculo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val marca: String,
    val modelo: String,
    val anio: Int,
    val kilometraje: Int,
    val tipoVehiculo: String,

    // Identifica al usuario propietario del vehículo.
    // null se utilizará temporalmente para los datos antiguos.
    val usuarioId: Int? = null
)