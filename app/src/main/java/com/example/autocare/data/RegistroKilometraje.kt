
package com.example.autocare.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "registros_kilometraje",
    foreignKeys = [
        ForeignKey(
            entity = Vehiculo::class,
            parentColumns = ["id"],
            childColumns = ["vehiculoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["vehiculoId"])]
)
data class RegistroKilometraje(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Vehículo al que pertenece el registro
    val vehiculoId: Int,

    // Fecha en que se registra el kilometraje
    val fecha: Long = System.currentTimeMillis(),

    // Kilometraje total indicado por el odómetro
    val kilometrajeActual: Int
)
