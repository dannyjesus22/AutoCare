
package com.example.autocare

import android.app.Application
import com.example.autocare.data.ConfiguracionDataStore
import com.example.autocare.data.VehiculoRepository
import com.example.autocare.data.RegistroKilometrajeRepository
import com.example.autocare.data.MantenimientoRepository
import com.example.autocare.data.usuarios.UsuarioRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AplicacionAutoCare : Application() {

    // Guarda las preferencias y la sesión del usuario.
    @Inject
    lateinit var configuracionDataStore: ConfiguracionDataStore

    // Repositorio de vehículos.
    @Inject
    lateinit var vehiculoRepository: VehiculoRepository

    // Repositorio de kilometraje.
    @Inject
    lateinit var registroKilometrajeRepository: RegistroKilometrajeRepository

    // Repositorio de mantenimiento.
    @Inject
    lateinit var mantenimientoRepository: MantenimientoRepository

    // Repositorio de usuarios.
    @Inject
    lateinit var usuarioRepository: UsuarioRepository
}
