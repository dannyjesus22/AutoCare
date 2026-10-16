package com.example.autocare.data.repository

import com.example.autocare.data.entities.RegistroKilometraje
import kotlinx.coroutines.flow.Flow

// Contrato que define las operaciones del historial de kilometraje.
interface IRegistroKilometrajeRepository {

    // Consultar todos los registros de un vehículo.
    fun obtenerRegistrosPorVehiculo(
        vehiculoId: Int
    ): Flow<List<RegistroKilometraje>>

    // Guardar una nueva lectura de kilometraje.
    suspend fun guardarKilometraje(
        vehiculoId: Int,
        kilometrajeActual: Int
    )

    // Consultar el último registro guardado.
    suspend fun obtenerUltimoRegistro(
        vehiculoId: Int
    ): RegistroKilometraje?

    // Eliminar un registro del historial.
    suspend fun eliminarRegistro(
        registro: RegistroKilometraje
    )
}