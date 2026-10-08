
package com.example.autocare.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VehiculoDao {

    // Guardar un vehículo
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVehiculo(vehiculo: Vehiculo)

    // Consultar todos los vehículos
    @Query("SELECT * FROM vehiculos ORDER BY id DESC")
    fun obtenerVehiculos(): Flow<List<Vehiculo>>

    // Consultar un vehículo por su ID
    @Query("SELECT * FROM vehiculos WHERE id = :id")
    suspend fun obtenerVehiculoPorId(id: Int): Vehiculo?

    // Actualizar un vehículo existente
    @Update
    suspend fun actualizarVehiculo(vehiculo: Vehiculo)

    // Eliminar un vehículo
    @Delete
    suspend fun eliminarVehiculo(vehiculo: Vehiculo)
}
