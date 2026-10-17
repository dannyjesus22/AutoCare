package com.example.autocare.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.autocare.data.entities.Vehiculo
import kotlinx.coroutines.flow.Flow

@Dao
interface VehiculoDao {

    // Guardar un vehículo.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVehiculo(vehiculo: Vehiculo)

    // Consulta existente: se conserva temporalmente
    // para no romper otras partes de AutoCare.
    @Query("SELECT * FROM vehiculos ORDER BY id DESC")
    fun obtenerVehiculos(): Flow<List<Vehiculo>>

    // NUEVO: obtener solo los vehículos de una cuenta.
    @Query("""
        SELECT * FROM vehiculos
        WHERE usuarioId = :usuarioId
        ORDER BY id DESC
    """)
    fun obtenerVehiculosPorUsuario(
        usuarioId: Int
    ): Flow<List<Vehiculo>>

    // Consulta existente por ID.
    @Query("SELECT * FROM vehiculos WHERE id = :id")
    suspend fun obtenerVehiculoPorId(id: Int): Vehiculo?

    // NUEVO: buscar un vehículo verificando su propietario.
    @Query("""
        SELECT * FROM vehiculos
        WHERE id = :id AND usuarioId = :usuarioId
        LIMIT 1
    """)
    suspend fun obtenerVehiculoDeUsuario(
        id: Int,
        usuarioId: Int
    ): Vehiculo?

    // NUEVO: asignar los vehículos antiguos a la primera cuenta.
    @Query("""
        UPDATE vehiculos
        SET usuarioId = :usuarioId
        WHERE usuarioId IS NULL
    """)
    suspend fun asignarVehiculosAntiguos(
        usuarioId: Int
    )

    // Actualizar un vehículo existente.
    @Update
    suspend fun actualizarVehiculo(vehiculo: Vehiculo)

    // Eliminar un vehículo.
    @Delete
    suspend fun eliminarVehiculo(vehiculo: Vehiculo)
}