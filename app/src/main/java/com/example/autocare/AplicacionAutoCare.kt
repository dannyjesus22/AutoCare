
package com.example.autocare

import android.app.Application
import androidx.room.Room

import com.example.autocare.data.BaseDatosAutoCare
import com.example.autocare.data.MIGRACION_1_2
import com.example.autocare.data.VehiculoRepository
import com.example.autocare.data.RegistroKilometrajeRepository
import com.example.autocare.data.MantenimientoRepository

class AplicacionAutoCare : Application() {

    // Crear una sola instancia de la base de datos
    val baseDatos by lazy {
        Room.databaseBuilder(
            applicationContext,
            BaseDatosAutoCare::class.java,
            "autocare_database"
        )
            // Migrar de versión 1 a 2 sin borrar los datos
            .addMigrations(MIGRACION_1_2)
            .build()
    }

    // Repository de vehículos
    val vehiculoRepository by lazy {
        VehiculoRepository(
            baseDatos.vehiculoDao()
        )
    }

    // Repository de kilometraje
    val registroKilometrajeRepository by lazy {
        RegistroKilometrajeRepository(
            registroKilometrajeDao = baseDatos.registroKilometrajeDao(),
            vehiculoDao = baseDatos.vehiculoDao(),
            baseDatos = baseDatos
        )
    }

    // Repository de mantenimiento
    val mantenimientoRepository by lazy {
        MantenimientoRepository(
            baseDatos.mantenimientoDao()
        )
    }
}
