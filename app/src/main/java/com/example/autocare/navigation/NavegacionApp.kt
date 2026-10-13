
package com.example.autocare.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.flowOf

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
import com.example.autocare.viewmodel.RegistroKilometrajeViewModel
import com.example.autocare.viewmodel.RegistroKilometrajeViewModelFactory
import com.example.autocare.data.RegistroKilometraje


@Composable
fun NavegacionApp() {

    // Controla qué pantalla se muestra
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


    // Crear el ViewModel del kilometraje
    val kilometrajeFactory = remember(aplicacion) {
        RegistroKilometrajeViewModelFactory(
            aplicacion.registroKilometrajeRepository
        )
    }

    val kilometrajeViewModel: RegistroKilometrajeViewModel = viewModel(
        factory = kilometrajeFactory
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

            // Obtenemos los vehículos registrados en Room.
            // collectAsState permite actualizar la pantalla
            // automáticamente cuando cambian los datos.
            val vehiculos by aplicacion.vehiculoRepository
                .todosLosVehiculos
                .collectAsState(initial = emptyList())

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
                },

                // Enviamos los vehículos reales a HomeScreen.
                vehiculos = vehiculos
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

            // Obtener los vehículos reales desde Room
            val vehiculos by aplicacion.vehiculoRepository
                .todosLosVehiculos
                .collectAsState(initial = emptyList())

            PantallaKilometraje(
                onVolver = {
                    pantallaActual = "inicio"
                },

                // Enviar los vehículos a la pantalla
                vehiculos = vehiculos,


                // Guardar el nuevo kilometraje total en Room
                onGuardarKilometraje = { vehiculoId, kilometrajeActual ->
                    kilometrajeViewModel.guardarKilometraje(
                        vehiculoId = vehiculoId,
                        kilometrajeActual = kilometrajeActual
                    )
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

            // Observamos los vehículos registrados en Room.
            val vehiculos by aplicacion.vehiculoRepository
                .todosLosVehiculos
                .collectAsStateWithLifecycle(
                    initialValue = emptyList()
                )

            // Estado del vehículo seleccionado.
            var vehiculoSeleccionadoId by remember {
                mutableStateOf<Int?>(null)
            }

            // Si no hay selección, usamos el primer vehículo.
            val vehiculoId = vehiculos.find {
                it.id == vehiculoSeleccionadoId
            }?.id ?: vehiculos.firstOrNull()?.id

            // Consultamos los registros del vehículo seleccionado.
            val registros by remember(vehiculoId) {

                if (vehiculoId != null) {

                    aplicacion.registroKilometrajeRepository
                        .obtenerRegistrosPorVehiculo(vehiculoId)

                } else {

                    flowOf(emptyList<RegistroKilometraje>())
                }

            }.collectAsStateWithLifecycle(
                initialValue = emptyList()
            )

            // Enviamos los datos a la pantalla.
            PantallaHistorial(
                onVolver = {
                    pantallaActual = "inicio"
                },
                vehiculos = vehiculos,
                registros = registros,
                vehiculoSeleccionadoId = vehiculoId,
                onSeleccionarVehiculo = { id ->
                    vehiculoSeleccionadoId = id
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
