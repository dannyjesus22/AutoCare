package com.example.autocare.data

import kotlinx.coroutines.flow.Flow

class MantenimientoRepository(private val mantenimientoDao: MantenimientoDao) {

    fun obtenerMantenimientosPorVehiculo(vehiculoId: Int): Flow<List<Mantenimiento>> {
        return mantenimientoDao.obtenerMantenimientosPorVehiculo(vehiculoId)
    }

    suspend fun obtenerMantenimientoPorId(id: Int): Mantenimiento? {
        return mantenimientoDao.obtenerMantenimientoPorId(id)
    }

    suspend fun insertarMantenimiento(mantenimiento: Mantenimiento) {
        mantenimientoDao.insertarMantenimiento(mantenimiento)
    }

    suspend fun actualizarMantenimiento(mantenimiento: Mantenimiento) {
        mantenimientoDao.actualizarMantenimiento(mantenimiento)
    }

    suspend fun eliminarMantenimiento(mantenimiento: Mantenimiento) {
        mantenimientoDao.eliminarMantenimiento(mantenimiento)
    }
}