
package com.example.autocare.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Vehiculo::class],
    version = 1,
    exportSchema = false
)
abstract class BaseDatosAutoCare : RoomDatabase() {

    abstract fun vehiculoDao(): VehiculoDao

}
