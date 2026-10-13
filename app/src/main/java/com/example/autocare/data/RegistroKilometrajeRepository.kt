
package com.example.autocare.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow

// Implementación del contrato IRegistroKilometrajeRepository.
// Aquí se encuentra la lógica real para trabajar con Room.
class RegistroKilometrajeRepository(
    private val registroKilometrajeDao: RegistroKilometrajeDao,
    private val vehiculoDao: VehiculoDao,
    private val baseDatos: BaseDatosAutoCare
) : IRegistroKilometrajeRepository {

    // Consultar el historial de un vehículo.
    override fun obtenerRegistrosPorVehiculo(
        vehiculoId: Int
    ): Flow<List<RegistroKilometraje>> {
        return registroKilometrajeDao.obtenerRegistrosPorVehiculo(vehiculoId)
    }

    // Guardar el kilometraje y actualizar el vehículo.
    override suspend fun guardarKilometraje(
        vehiculoId: Int,
        kilometrajeActual: Int
    ) {
        baseDatos.withTransaction {

            // Buscar el vehículo en Room.
            val vehiculo = vehiculoDao.obtenerVehiculoPorId(vehiculoId)
                ?: throw IllegalArgumentException("Vehículo no encontrado")

            // Evitar guardar un kilometraje menor o igual al actual.
            require(kilometrajeActual > vehiculo.kilometraje) {
                "El nuevo kilometraje debe ser mayor al actual"
            }

            // Actualizar el kilometraje del vehículo.
            vehiculoDao.actualizarVehiculo(
                vehiculo.copy(kilometraje = kilometrajeActual)
            )

            // Guardar una lectura en el historial.
            val registro = RegistroKilometraje(
                vehiculoId = vehiculoId,
                kilometrajeActual = kilometrajeActual
            )

            registroKilometrajeDao.insertarRegistro(registro)
        }
    }

    // Consultar el último registro guardado.
    override suspend fun obtenerUltimoRegistro(
        vehiculoId: Int
    ): RegistroKilometraje? {
        return registroKilometrajeDao.obtenerUltimoRegistro(vehiculoId)
    }

    // Eliminar un registro del historial.
    override suspend fun eliminarRegistro(
        registro: RegistroKilometraje
    ) {
        registroKilometrajeDao.eliminarRegistro(registro)
    }
}
