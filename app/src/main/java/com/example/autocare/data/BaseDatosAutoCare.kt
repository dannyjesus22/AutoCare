
package com.example.autocare.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.autocare.data.dao.MantenimientoDao
import com.example.autocare.data.dao.RegistroKilometrajeDao
import com.example.autocare.data.dao.VehiculoDao
import com.example.autocare.data.entities.Mantenimiento
import com.example.autocare.data.entities.RegistroKilometraje
import com.example.autocare.data.entities.Vehiculo
import com.example.autocare.data.entities.Usuario
import com.example.autocare.data.dao.UsuarioDao

@Database(
    entities = [
        Vehiculo::class,
        RegistroKilometraje::class,
        Mantenimiento::class,
        Usuario::class
    ],
    version = 3,
    exportSchema = false
)
abstract class BaseDatosAutoCare : RoomDatabase() {

    // Operaciones de vehículos.
    abstract fun vehiculoDao(): VehiculoDao

    // Operaciones de kilometraje.
    abstract fun registroKilometrajeDao(): RegistroKilometrajeDao

    // Operaciones de mantenimiento.
    abstract fun mantenimientoDao(): MantenimientoDao

    // Operaciones de cuentas de usuario.
    abstract fun usuarioDao(): UsuarioDao
}
