package com.example.autocare

import android.app.Application
import androidx.room.Room
import com.example.autocare.data.BaseDatosAutoCare
import com.example.autocare.data.ConfiguracionDataStore
import com.example.autocare.data.MIGRACION_1_2
import com.example.autocare.data.MIGRACION_2_3
import com.example.autocare.data.VehiculoRepository
import com.example.autocare.data.RegistroKilometrajeRepository
import com.example.autocare.data.MantenimientoRepository
import com.example.autocare.data.usuarios.UsuarioRepository
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AplicacionAutoCare : Application() {

    // Guarda las preferencias y la sesión del usuario.
    val configuracionDataStore by lazy {
        ConfiguracionDataStore(applicationContext)
    }

    // Crear una sola instancia de Room.
    val baseDatos by lazy {
        Room.databaseBuilder(
            applicationContext,
            BaseDatosAutoCare::class.java,
            "autocare_database"
        )
            .addMigrations(
                MIGRACION_1_2,
                MIGRACION_2_3
            )
            .build()
    }

    // Repository de vehículos.
    val vehiculoRepository by lazy {
        VehiculoRepository(
            baseDatos.vehiculoDao()
        )
    }

    // Repository de kilometraje.
    val registroKilometrajeRepository by lazy {
        RegistroKilometrajeRepository(
            registroKilometrajeDao = baseDatos.registroKilometrajeDao(),
            vehiculoDao = baseDatos.vehiculoDao(),
            baseDatos = baseDatos
        )
    }

    // Repository de mantenimiento.
    val mantenimientoRepository by lazy {
        MantenimientoRepository(
            baseDatos.mantenimientoDao()
        )
    }

    // Repository de usuarios.
    val usuarioRepository by lazy {
        UsuarioRepository(
            baseDatos.usuarioDao()
        )
    }
}