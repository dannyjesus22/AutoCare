package com.example.autocare.data.repository

import com.example.autocare.data.dao.VehiculoDao
import com.example.autocare.data.entities.Vehiculo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VehiculoRepository @Inject constructor(
    private val vehiculoDao: VehiculoDao
) {

    // Consulta antigua. Se mantiene temporalmente
    // para no romper las pantallas existentes.
    val todosLosVehiculos: Flow<List<Vehiculo>> =
        vehiculoDao.obtenerVehiculos()

    // Consultar los vehículos de una cuenta.
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

    // Buscar un vehículo de una cuenta específica.
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