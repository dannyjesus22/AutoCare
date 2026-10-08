package com.example.autocare.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "mantenimientos",
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
data class Mantenimiento(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val vehiculoId: Int,
    val titulo: String,
    val tipo: String,
    val kilometrajeRealizado: Int,
    val proximoKilometraje: Int?,
    val fecha: String,
    val proximaFecha: String?,
    val costo: Double,
    val notas: String = "",
    val completado: Boolean = true
)