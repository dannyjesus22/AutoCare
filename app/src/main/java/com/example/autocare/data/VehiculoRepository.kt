
package com.example.autocare.data

import kotlinx.coroutines.flow.Flow

class VehiculoRepository(
    private val vehiculoDao: VehiculoDao
) {

    // Consulta antigua. Se mantiene temporalmente
    // para no romper las pantallas existentes.
    val todosLosVehiculos: Flow<List<Vehiculo>> =
        vehiculoDao.obtenerVehiculos()

    // NUEVO: consultar los vehículos de una cuenta.
    fun obtenerVehiculosPorUsuario(
        usuarioId: Int
    ): Flow<List<Vehiculo>> {
        return vehiculoDao.obtenerVehiculosPorUsuario(usuarioId)
    }

    // Guardar un vehículo.
    suspend fun insertarVehiculo(vehiculo: Vehiculo) {
        vehiculoDao.insertarVehiculo(vehiculo)
    }

    // Buscar un vehículo por su ID.
    suspend fun obtenerVehiculoPorId(id: Int): Vehiculo? {
        return vehiculoDao.obtenerVehiculoPorId(id)
    }

    // NUEVO: buscar un vehículo de una cuenta específica.
    suspend fun obtenerVehiculoDeUsuario(
        id: Int,
        usuarioId: Int
    ): Vehiculo? {
        return vehiculoDao.obtenerVehiculoDeUsuario(
            id,
            usuarioId
        )
    }

    // Actualizar los datos de un vehículo.
    suspend fun actualizarVehiculo(vehiculo: Vehiculo) {
        vehiculoDao.actualizarVehiculo(vehiculo)
    }

    // Eliminar un vehículo.
    suspend fun eliminarVehiculo(vehiculo: Vehiculo) {
        vehiculoDao.eliminarVehiculo(vehiculo)
    }
}
