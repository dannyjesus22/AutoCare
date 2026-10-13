
package com.example.autocare.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Vehiculo::class,
        RegistroKilometraje::class,
        Mantenimiento::class
    ],
    version = 2,
    exportSchema = false
)
abstract class BaseDatosAutoCare : RoomDatabase() {

    // Operaciones de vehículos
    abstract fun vehiculoDao(): VehiculoDao

    // Operaciones de kilometraje
    abstract fun registroKilometrajeDao(): RegistroKilometrajeDao

    // Operaciones de mantenimiento
    abstract fun mantenimientoDao(): MantenimientoDao
}
