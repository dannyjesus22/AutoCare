
package com.example.autocare.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
import com.example.autocare.viewmodel.HistorialUiState
import com.example.autocare.viewmodel.HistorialViewModel
import com.example.autocare.viewmodel.HistorialViewModelFactory
import com.example.autocare.viewmodel.RegistroKilometrajeViewModel
import com.example.autocare.viewmodel.RegistroKilometrajeViewModelFactory
import com.example.autocare.viewmodel.VehiculoViewModel
import com.example.autocare.viewmodel.VehiculoViewModelFactory

@Composable
fun NavegacionPrueba() {

    // Controlador de navegación de AutoCare.
    val navController = rememberNavController()


    // Contenedor de las pantallas de AutoCare.
    NavHost(
        navController = navController,
        startDestination = Rutas.SPLASH
    ) {

        // 1. Pantalla de bienvenida.
        composable(Rutas.SPLASH) {

            SplashScreen(
                onIrLogin = {
                    navController.navigate(Rutas.LOGIN)
                }
            )
        }

        // 2. Inicio de sesión.
        composable(Rutas.LOGIN) {

            LoginScreen(
                onIniciarSesion = {
                    navController.navigate(Rutas.INICIO)
                },
                onIrRegistro = {
                    navController.navigate(Rutas.REGISTRO)
                }
            )
        }

        // 3. Registro de usuarios.
        composable(Rutas.REGISTRO) {

            RegisterScreen(
                onIrLogin = {
                    navController.navigate(Rutas.LOGIN)
                }
            )
        }

        // 4. Pantalla principal.
        composable(Rutas.INICIO) {

            val contexto = LocalContext.current.applicationContext
            val aplicacion = contexto as AplicacionAutoCare

            // Consultamos los vehículos almacenados en Room.
            val vehiculos by aplicacion.vehiculoRepository
                .todosLosVehiculos
                .collectAsStateWithLifecycle(
                    initialValue = emptyList()
                )

            HomeScreen(
                onIrKilometraje = {
                    navController.navigate(Rutas.KILOMETRAJE)
                },
                onIrMantenimiento = {
                    navController.navigate(Rutas.MANTENIMIENTO)
                },
                onIrHistorial = {
                    navController.navigate(Rutas.HISTORIAL)
                },
                onIrConfiguracion = {
                    navController.navigate(Rutas.CONFIGURACION)
                },
                vehiculos = vehiculos
            )
        }

        // 5. Registro de kilometraje.
        composable(Rutas.KILOMETRAJE) {

            val contexto = LocalContext.current.applicationContext
            val aplicacion = contexto as AplicacionAutoCare

            // Obtenemos los vehículos registrados.
            val vehiculos by aplicacion.vehiculoRepository
                .todosLosVehiculos
                .collectAsStateWithLifecycle(
                    initialValue = emptyList()
                )

            // Creamos el ViewModel utilizando su Factory.
            val kilometrajeViewModel: RegistroKilometrajeViewModel =
                viewModel(
                    factory = RegistroKilometrajeViewModelFactory(
                        aplicacion.registroKilometrajeRepository
                    )
                )

            PantallaKilometraje(
                onVolver = {
                    navController.popBackStack()
                },
                vehiculos = vehiculos,
                onGuardarKilometraje = { vehiculoId, kilometrajeActual ->

                    kilometrajeViewModel.guardarKilometraje(
                        vehiculoId = vehiculoId,
                        kilometrajeActual = kilometrajeActual
                    )
                }
            )
        }

        // 6. Historial de kilometraje.
        composable(Rutas.HISTORIAL) {

            val contexto = LocalContext.current.applicationContext
            val aplicacion = contexto as AplicacionAutoCare

            // Obtenemos los vehículos guardados en Room.
            val vehiculos by aplicacion.vehiculoRepository
                .todosLosVehiculos
                .collectAsStateWithLifecycle(
                    initialValue = emptyList()
                )

            // Identificador del vehículo seleccionado.
            var vehiculoSeleccionadoId by remember {
                mutableStateOf<Int?>(null)
            }

            // Seleccionamos el vehículo correspondiente.
            // Si no hay selección, usamos el primero disponible.
            val vehiculoId = vehiculos.find {
                it.id == vehiculoSeleccionadoId
            }?.id ?: vehiculos.firstOrNull()?.id

            // ViewModel del historial.
            val historialViewModel: HistorialViewModel = viewModel(
                factory = HistorialViewModelFactory(
                    aplicacion.registroKilometrajeRepository
                )
            )

            // Cargamos el historial cuando cambia el vehículo.
            LaunchedEffect(vehiculoId) {

                if (vehiculoId != null) {
                    historialViewModel.cargarHistorial(vehiculoId)
                }
            }

            // Observamos el estado del historial.
            val estadoHistorial by historialViewModel.uiState
                .collectAsStateWithLifecycle()

            // Extraemos los registros cuando la consulta termina.
            val registros = when (val estado = estadoHistorial) {

                is HistorialUiState.Success -> {
                    estado.registros
                }

                else -> {
                    emptyList()
                }
            }

            PantallaHistorial(
                onVolver = {
                    navController.popBackStack()
                },
                vehiculos = vehiculos,
                registros = registros,
                vehiculoSeleccionadoId = vehiculoId,
                onSeleccionarVehiculo = { id ->
                    vehiculoSeleccionadoId = id
                },
                estadoHistorial = estadoHistorial
            )
        }

        // 7. Pantalla de mantenimiento.
        composable(Rutas.MANTENIMIENTO) {

            PantallaMantenimiento(
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        // 8. Configuración de vehículos.
        composable(Rutas.CONFIGURACION) {

            val contexto = LocalContext.current.applicationContext
            val aplicacion = contexto as AplicacionAutoCare

            val vehiculoViewModel: VehiculoViewModel = viewModel(
                factory = VehiculoViewModelFactory(
                    aplicacion.vehiculoRepository
                )
            )

            PantallaConfiguracion(
                onVolver = {
                    navController.popBackStack()
                },
                onAgregarVehiculo = {
                    navController.navigate(Rutas.VEHICULO)
                },
                onEditarVehiculo = { vehiculo ->
                    navController.navigate(
                        Rutas.editarVehiculo(vehiculo.id)
                    )
                },
                vehiculoViewModel = vehiculoViewModel
            )
        }

        // 9. Registro y edición de vehículos.
        composable(Rutas.VEHICULO) {

            val contexto = LocalContext.current.applicationContext
            val aplicacion = contexto as AplicacionAutoCare

            val vehiculoViewModel: VehiculoViewModel = viewModel(
                factory = VehiculoViewModelFactory(
                    aplicacion.vehiculoRepository
                )
            )

            VehicleRegisterScreen(
                onVolver = {

                    navController.popBackStack()
                },
                vehiculoViewModel = vehiculoViewModel,
                vehiculoEditar = null
            )
        }

        // 10. Editar un vehículo utilizando su ID.
        composable(Rutas.EDITAR_VEHICULO) { entrada ->

            // Recuperamos el ID enviado por NavController.
            val vehiculoId = entrada.arguments
                ?.getString("vehiculoId")
                ?.toIntOrNull()

            val contexto = LocalContext.current.applicationContext
            val aplicacion = contexto as AplicacionAutoCare

            // Creamos el ViewModel con nuestra Factory.
            val vehiculoViewModel: VehiculoViewModel = viewModel(
                factory = VehiculoViewModelFactory(
                    aplicacion.vehiculoRepository
                )
            )

            // Consultamos los datos del vehículo en Room.
            var vehiculoEditar by remember(vehiculoId) {
                mutableStateOf<Vehiculo?>(null)
            }

            LaunchedEffect(vehiculoId) {
                vehiculoEditar = vehiculoId?.let { id ->
                    vehiculoViewModel.obtenerVehiculoPorId(id)
                }
            }

            // Mostramos el formulario cuando tenemos el vehículo.
            if (vehiculoEditar != null) {

                VehicleRegisterScreen(
                    onVolver = {
                        navController.popBackStack()
                    },
                    vehiculoViewModel = vehiculoViewModel,
                    vehiculoEditar = vehiculoEditar
                )
            }
        }


    }
}
