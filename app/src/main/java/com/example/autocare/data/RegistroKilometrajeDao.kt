
package com.example.autocare.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroKilometrajeDao {

    // Guardar un nuevo registro de kilometraje
    @Insert
    suspend fun insertarRegistro(
        registro: RegistroKilometraje
    )

    // Obtener el historial de un vehículo
    @Query(
        """
        SELECT * FROM registros_kilometraje
        WHERE vehiculoId = :vehiculoId
        ORDER BY fecha DESC, id DESC
        """
    )
    fun obtenerRegistrosPorVehiculo(
        vehiculoId: Int
    ): Flow<List<RegistroKilometraje>>

    // Consultar el último kilometraje registrado
    @Query(
        """
        SELECT * FROM registros_kilometraje
        WHERE vehiculoId = :vehiculoId
        ORDER BY fecha DESC, id DESC
        LIMIT 1
        """
    )
    suspend fun obtenerUltimoRegistro(
        vehiculoId: Int
    ): RegistroKilometraje?

    // Eliminar un registro de kilometraje
    @Delete
    suspend fun eliminarRegistro(
        registro: RegistroKilometraje
    )
}
