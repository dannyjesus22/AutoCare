
package com.example.autocare

import android.app.Application
import androidx.room.Room
import com.example.autocare.data.BaseDatosAutoCare
import com.example.autocare.data.VehiculoRepository

class AplicacionAutoCare : Application() {

    // Crear una sola instancia de la base de datos
    val baseDatos by lazy {
        Room.databaseBuilder(
            applicationContext,
            BaseDatosAutoCare::class.java,
            "autocare_database"
        ).build()
    }

    // Conectar el Repository con el DAO
    val vehiculoRepository by lazy {
        VehiculoRepository(baseDatos.vehiculoDao())
    }
}
