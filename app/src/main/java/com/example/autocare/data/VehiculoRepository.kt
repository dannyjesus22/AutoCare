
package com.example.autocare.data

import kotlinx.coroutines.flow.Flow

class VehiculoRepository(
    private val vehiculoDao: VehiculoDao
) {

    // Obtener todos los vehículos registrados
    val todosLosVehiculos: Flow<List<Vehiculo>> =
        vehiculoDao.obtenerVehiculos()

    // Guardar un nuevo vehículo
    suspend fun insertarVehiculo(vehiculo: Vehiculo) {
        vehiculoDao.insertarVehiculo(vehiculo)
    }

    // Buscar un vehículo por su ID
    suspend fun obtenerVehiculoPorId(id: Int): Vehiculo? {
        return vehiculoDao.obtenerVehiculoPorId(id)
    }

    // Actualizar los datos de un vehículo
    suspend fun actualizarVehiculo(vehiculo: Vehiculo) {
        vehiculoDao.actualizarVehiculo(vehiculo)
    }

    // Eliminar un vehículo
    suspend fun eliminarVehiculo(vehiculo: Vehiculo) {
        vehiculoDao.eliminarVehiculo(vehiculo)
    }
}
