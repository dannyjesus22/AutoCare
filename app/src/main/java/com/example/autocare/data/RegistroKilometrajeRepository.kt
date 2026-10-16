
package com.example.autocare.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class RegistroKilometrajeRepository @Inject constructor(
    private val registroKilometrajeDao: RegistroKilometrajeDao,
    private val vehiculoDao: VehiculoDao,
    private val baseDatos: BaseDatosAutoCare
) : IRegistroKilometrajeRepository {

    // Método existente: consultar registros por vehículo.
    // Se conserva por compatibilidad con el proyecto.
    override fun obtenerRegistrosPorVehiculo(
        vehiculoId: Int
    ): Flow<List<RegistroKilometraje>> {
        return registroKilometrajeDao
            .obtenerRegistrosPorVehiculo(vehiculoId)
    }

    // NUEVO: consultar registros verificando el propietario.
    suspend fun obtenerRegistrosDeUsuario(
        vehiculoId: Int,
        usuarioId: Int
    ): Flow<List<RegistroKilometraje>> {

        val vehiculo = vehiculoDao.obtenerVehiculoDeUsuario(
            vehiculoId,
            usuarioId
        )

        return if (vehiculo != null) {
            registroKilometrajeDao.obtenerRegistrosPorVehiculo(
                vehiculoId
            )
        } else {
            flowOf(emptyList())
        }
    }

    // Método existente: guardar kilometraje.
    // Se conserva para no romper las llamadas actuales.
    override suspend fun guardarKilometraje(
        vehiculoId: Int,
        kilometrajeActual: Int
    ) {
        guardarKilometrajeInterno(
            vehiculoId = vehiculoId,
            kilometrajeActual = kilometrajeActual,
            usuarioId = null
        )
    }

    // NUEVO: guardar kilometraje de un usuario concreto.
    suspend fun guardarKilometrajeDeUsuario(
        vehiculoId: Int,
        kilometrajeActual: Int,
        usuarioId: Int
    ) {
        require(usuarioId > 0) {
            "Usuario no válido"
        }

        guardarKilometrajeInterno(
            vehiculoId = vehiculoId,
            kilometrajeActual = kilometrajeActual,
            usuarioId = usuarioId
        )
    }

    // Lógica compartida para guardar kilometraje.
    private suspend fun guardarKilometrajeInterno(
        vehiculoId: Int,
        kilometrajeActual: Int,
        usuarioId: Int?
    ) {
        baseDatos.withTransaction {

            val vehiculo = if (usuarioId != null) {

                // Comprobar que el vehículo pertenece al usuario.
                vehiculoDao.obtenerVehiculoDeUsuario(
                    vehiculoId,
                    usuarioId
                )

            } else {

                // Compatibilidad temporal con las llamadas antiguas.
                vehiculoDao.obtenerVehiculoPorId(vehiculoId)
            } ?: throw IllegalArgumentException(
                "Vehículo no encontrado o no pertenece al usuario"
            )

            require(kilometrajeActual > vehiculo.kilometraje) {
                "El nuevo kilometraje debe ser mayor al actual"
            }

            // Actualizar kilometraje del vehículo.
            vehiculoDao.actualizarVehiculo(
                vehiculo.copy(
                    kilometraje = kilometrajeActual
                )
            )

            // Registrar la lectura en el historial.
            registroKilometrajeDao.insertarRegistro(
                RegistroKilometraje(
                    vehiculoId = vehiculoId,
                    kilometrajeActual = kilometrajeActual
                )
            )
        }
    }

    // Método existente: consultar el último registro.
    override suspend fun obtenerUltimoRegistro(
        vehiculoId: Int
    ): RegistroKilometraje? {
        return registroKilometrajeDao.obtenerUltimoRegistro(
            vehiculoId
        )
    }

    // NUEVO: consultar último registro del usuario.
    suspend fun obtenerUltimoRegistroDeUsuario(
        vehiculoId: Int,
        usuarioId: Int
    ): RegistroKilometraje? {

        val vehiculo = vehiculoDao.obtenerVehiculoDeUsuario(
            vehiculoId,
            usuarioId
        ) ?: return null

        return registroKilometrajeDao.obtenerUltimoRegistro(
            vehiculo.id
        )
    }

    // Método existente: eliminar un registro.
    override suspend fun eliminarRegistro(
        registro: RegistroKilometraje
    ) {
        registroKilometrajeDao.eliminarRegistro(registro)
    }

    // NUEVO: eliminar un registro solamente si
    // pertenece a un vehículo del usuario.
    suspend fun eliminarRegistroDeUsuario(
        registro: RegistroKilometraje,
        usuarioId: Int
    ) {
        baseDatos.withTransaction {

            val vehiculo = vehiculoDao.obtenerVehiculoDeUsuario(
                registro.vehiculoId,
                usuarioId
            ) ?: throw IllegalArgumentException(
                "No puedes eliminar registros de otro usuario"
            )

            registroKilometrajeDao.eliminarRegistro(registro)
        }
    }
}
