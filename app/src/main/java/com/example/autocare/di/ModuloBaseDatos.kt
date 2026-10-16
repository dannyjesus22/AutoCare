package com.example.autocare.di

import android.content.Context
import androidx.room.Room
import com.example.autocare.data.BaseDatosAutoCare
import com.example.autocare.data.MIGRACION_1_2
import com.example.autocare.data.MIGRACION_2_3
import com.example.autocare.data.VehiculoDao
import com.example.autocare.data.RegistroKilometrajeDao
import com.example.autocare.data.MantenimientoDao
import com.example.autocare.data.usuarios.UsuarioDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModuloBaseDatos {

    @Provides
    @Singleton
    fun proporcionarBaseDatos(
        @ApplicationContext context: Context
    ): BaseDatosAutoCare {
        return Room.databaseBuilder(
            context,
            BaseDatosAutoCare::class.java,
            "autocare_database"
        )
            .addMigrations(
                MIGRACION_1_2,
                MIGRACION_2_3
            )
            .build()
    }

    @Provides
    fun proporcionarVehiculoDao(
        baseDatos: BaseDatosAutoCare
    ): VehiculoDao {
        return baseDatos.vehiculoDao()
    }

    @Provides
    fun proporcionarRegistroKilometrajeDao(
        baseDatos: BaseDatosAutoCare
    ): RegistroKilometrajeDao {
        return baseDatos.registroKilometrajeDao()
    }

    @Provides
    fun proporcionarMantenimientoDao(
        baseDatos: BaseDatosAutoCare
    ): MantenimientoDao {
        return baseDatos.mantenimientoDao()
    }

    @Provides
    fun proporcionarUsuarioDao(
        baseDatos: BaseDatosAutoCare
    ): UsuarioDao {
        return baseDatos.usuarioDao()
    }
}