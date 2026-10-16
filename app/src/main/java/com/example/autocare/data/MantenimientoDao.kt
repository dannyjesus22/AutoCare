package com.example.autocare.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MantenimientoDao {

    @Query("SELECT * FROM mantenimientos WHERE vehiculoId = :vehiculoId ORDER BY fecha DESC")
    fun obtenerMantenimientosPorVehiculo(vehiculoId: Int): Flow<List<Mantenimiento>>

    @Query("SELECT * FROM mantenimientos WHERE vehiculoId = :vehiculoId AND completado = 0 ORDER BY fecha DESC")
    fun obtenerMantenimientosPendientesPorVehiculo(vehiculoId: Int): Flow<List<Mantenimiento>>

    @Query("SELECT * FROM mantenimientos WHERE id = :id")
    suspend fun obtenerMantenimientoPorId(id: Int): Mantenimiento?

    @Query("UPDATE mantenimientos SET completado = :completado WHERE id = :id")
    suspend fun actualizarEstadoCompletado(id: Int, completado: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMantenimiento(mantenimiento: Mantenimiento)

    @Update
    suspend fun actualizarMantenimiento(mantenimiento: Mantenimiento)

    @Delete
    suspend fun eliminarMantenimiento(mantenimiento: Mantenimiento)
}