
package com.example.autocare.data

import kotlinx.coroutines.flow.Flow

class RegistroKilometrajeRepository(
    private val registroKilometrajeDao: RegistroKilometrajeDao
) {

    // Obtener el historial de kilometraje de un vehículo
    fun obtenerRegistrosPorVehiculo(
        vehiculoId: Int
    ): Flow<List<RegistroKilometraje>> {
        return registroKilometrajeDao.obtenerRegistrosPorVehiculo(
            vehiculoId
        )
    }

    // Guardar un nuevo registro de kilometraje
    suspend fun insertarRegistro(
        registro: RegistroKilometraje
    ) {
        registroKilometrajeDao.insertarRegistro(registro)
    }

    // Consultar el último kilometraje registrado
    suspend fun obtenerUltimoRegistro(
        vehiculoId: Int
    ): RegistroKilometraje? {
        return registroKilometrajeDao.obtenerUltimoRegistro(
            vehiculoId
        )
    }

    // Eliminar un registro de kilometraje
    suspend fun eliminarRegistro(
        registro: RegistroKilometraje
    ) {
        registroKilometrajeDao.eliminarRegistro(registro)
    }
}
