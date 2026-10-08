
package com.example.autocare.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.autocare.AplicacionAutoCare
import com.example.autocare.data.Vehiculo
import com.example.autocare.screens.HomeScreen
import com.example.autocare.screens.LoginScreen
import com.example.autocare.screens.PantallaConfiguracion
import com.example.autocare.screens.PantallaHistorial
import com.example.autocare.screens.PantallaKilometraje
import com.example.autocare.screens.PantallaMantenimiento
import com.example.autocare.screens.RegisterScreen
import com.example.autocare.screens.SplashScreen
import com.example.autocare.screens.VehicleRegisterScreen
import com.example.autocare.viewmodel.VehiculoViewModel
import com.example.autocare.viewmodel.VehiculoViewModelFactory

@Composable
fun NavegacionApp() {

    // Pantalla inicial de AutoCare
    var pantallaActual by remember {
        mutableStateOf("splash")
    }

    // Vehículo seleccionado para editar
    var vehiculoSeleccionado by remember {
        mutableStateOf<Vehiculo?>(null)
    }

    // Obtener el Repository desde nuestra aplicación
    val contexto = LocalContext.current.applicationContext
    val aplicacion = contexto as AplicacionAutoCare

    val factory = remember(aplicacion) {
        VehiculoViewModelFactory(
            aplicacion.vehiculoRepository
        )
    }

    val vehiculoViewModel: VehiculoViewModel = viewModel(
        factory = factory
    )

    when (pantallaActual) {

        "splash" -> {
            SplashScreen(
                onIrLogin = {
                    pantallaActual = "login"
                }
            )
        }

        "login" -> {
            LoginScreen(
                onIniciarSesion = {
                    pantallaActual = "inicio"
                },
                onIrRegistro = {
                    pantallaActual = "registro"
                }
            )
        }

        "registro" -> {
            RegisterScreen(
                onIrLogin = {
                    pantallaActual = "login"
                }
            )
        }


        "inicio" -> {
            HomeScreen(
                onIrKilometraje = {
                    pantallaActual = "kilometraje"
                },
                onIrMantenimiento = {
                    pantallaActual = "mantenimiento"
                },
                onIrHistorial = {
                    pantallaActual = "historial"
                },
                onIrConfiguracion = {
                    pantallaActual = "configuracion"
                }
            )
        }



        "vehiculo" -> {
            VehicleRegisterScreen(
                onVolver = {
                    vehiculoSeleccionado = null
                    pantallaActual = "configuracion"
                },
                vehiculoViewModel = vehiculoViewModel,
                vehiculoEditar = vehiculoSeleccionado
            )
        }


        "kilometraje" -> {
            PantallaKilometraje(
                onVolver = {
                    pantallaActual = "inicio"
                }
            )
        }

        "mantenimiento" -> {
            PantallaMantenimiento(
                onVolver = {
                    pantallaActual = "inicio"
                }
            )
        }

        "historial" -> {
            PantallaHistorial(
                onVolver = {
                    pantallaActual = "inicio"
                }
            )
        }

        "configuracion" -> {
            PantallaConfiguracion(
                onVolver = {
                    pantallaActual = "inicio"
                },
                onAgregarVehiculo = {
                    vehiculoSeleccionado = null
                    pantallaActual = "vehiculo"
                },
                onEditarVehiculo = { vehiculo ->
                    vehiculoSeleccionado = vehiculo
                    pantallaActual = "vehiculo"
                },
                vehiculoViewModel = vehiculoViewModel
            )
        }
    }
}
