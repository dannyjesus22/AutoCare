
package com.example.autocare.data

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
    val tipoVehiculo: String
)
