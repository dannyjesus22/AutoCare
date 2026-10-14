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
import com.example.autocare.viewmodel.MantenimientoViewModel
import com.example.autocare.viewmodel.MantenimientoViewModelFactory
import com.example.autocare.viewmodel.RegistroKilometrajeViewModel
import com.example.autocare.viewmodel.RegistroKilometrajeViewModelFactory
import com.example.autocare.viewmodel.VehiculoViewModel
import com.example.autocare.viewmodel.VehiculoViewModelFactory

@Composable
fun NavegacionPrueba() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.SPLASH
    ) {

        composable(Rutas.SPLASH) {
            SplashScreen(
                onIrLogin = { navController.navigate(Rutas.LOGIN) }
            )
        }

        composable(Rutas.LOGIN) {
            LoginScreen(
                onIniciarSesion = { navController.navigate(Rutas.INICIO) },
                onIrRegistro = { navController.navigate(Rutas.REGISTRO) }
            )
        }

        composable(Rutas.REGISTRO) {
            RegisterScreen(
                onIrLogin = { navController.navigate(Rutas.LOGIN) }
            )
        }

        composable(Rutas.INICIO) {
            val contexto = LocalContext.current.applicationContext
            val aplicacion = contexto as AplicacionAutoCare

            val vehiculos by aplicacion.vehiculoRepository
                .todosLosVehiculos
                .collectAsStateWithLifecycle(initialValue = emptyList())

            HomeScreen(
                onIrKilometraje = { navController.navigate(Rutas.KILOMETRAJE) },
                onIrMantenimiento = { navController.navigate(Rutas.MANTENIMIENTO) },
                onIrHistorial = { navController.navigate(Rutas.HISTORIAL) },
                onIrConfiguracion = { navController.navigate(Rutas.CONFIGURACION) },
                vehiculos = vehiculos
            )
        }

        composable(Rutas.KILOMETRAJE) {
            val contexto = LocalContext.current.applicationContext
            val aplicacion = contexto as AplicacionAutoCare

            val vehiculos by aplicacion.vehiculoRepository
                .todosLosVehiculos
                .collectAsStateWithLifecycle(initialValue = emptyList())

            val kilometrajeViewModel: RegistroKilometrajeViewModel = viewModel(
                factory = RegistroKilometrajeViewModelFactory(
                    aplicacion.registroKilometrajeRepository
                )
            )

            val mantenimientoViewModel: MantenimientoViewModel = viewModel(
                factory = MantenimientoViewModelFactory(
                    aplicacion.mantenimientoRepository
                )
            )

            PantallaKilometraje(
                onVolver = { navController.popBackStack() },
                vehiculos = vehiculos,
                onGuardarKilometraje = { vehiculoId, kilometrajeActual ->
                    // 1. Guardar kilometraje en Room
                    kilometrajeViewModel.guardarKilometraje(
                        vehiculoId = vehiculoId,
                        kilometrajeActual = kilometrajeActual
                    )

                    // 2. Verificar si dispara alertas de mantenimiento por kilometraje
                    mantenimientoViewModel.verificarNotificacionKilometraje(
                        vehiculoId = vehiculoId,
                        nuevoKilometraje = kilometrajeActual,
                        context = contexto
                    )
                }
            )
        }

        composable(Rutas.HISTORIAL) {
            val contexto = LocalContext.current.applicationContext
            val aplicacion = contexto as AplicacionAutoCare

            val vehiculos by aplicacion.vehiculoRepository
                .todosLosVehiculos
                .collectAsStateWithLifecycle(initialValue = emptyList())

            var vehiculoSeleccionadoId by remember { mutableStateOf<Int?>(null) }

            val vehiculoId = vehiculos.find {
                it.id == vehiculoSeleccionadoId
            }?.id ?: vehiculos.firstOrNull()?.id

            val historialViewModel: HistorialViewModel = viewModel(
                factory = HistorialViewModelFactory(
                    aplicacion.registroKilometrajeRepository
                )
            )

            val mantenimientoViewModel: MantenimientoViewModel = viewModel(
                factory = MantenimientoViewModelFactory(
                    aplicacion.mantenimientoRepository
                )
            )

            LaunchedEffect(vehiculoId) {
                if (vehiculoId != null) {
                    historialViewModel.cargarHistorial(vehiculoId)
                }
            }

            val estadoHistorial by historialViewModel.uiState.collectAsStateWithLifecycle()

            val mantenimientos by mantenimientoViewModel
                .obtenerMantenimientosPorVehiculo(vehiculoId ?: 1)
                .collectAsStateWithLifecycle(initialValue = emptyList())

            val registros = when (val estado = estadoHistorial) {
                is HistorialUiState.Success -> estado.registros
                else -> emptyList()
            }

            PantallaHistorial(
                onVolver = { navController.popBackStack() },
                vehiculos = vehiculos,
                registros = registros,
                registrosMantenimiento = mantenimientos,
                vehiculoSeleccionadoId = vehiculoId,
                onSeleccionarVehiculo = { id -> vehiculoSeleccionadoId = id },
                estadoHistorial = estadoHistorial
            )
        }

        composable(Rutas.MANTENIMIENTO) {
            val contexto = LocalContext.current.applicationContext
            val aplicacion = contexto as AplicacionAutoCare

            val vehiculos by aplicacion.vehiculoRepository
                .todosLosVehiculos
                .collectAsStateWithLifecycle(initialValue = emptyList())

            val mantenimientoViewModel: MantenimientoViewModel = viewModel(
                factory = MantenimientoViewModelFactory(
                    aplicacion.mantenimientoRepository
                )
            )

            PantallaMantenimiento(
                vehiculos = vehiculos,
                viewModel = mantenimientoViewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.CONFIGURACION) {
            val contexto = LocalContext.current.applicationContext
            val aplicacion = contexto as AplicacionAutoCare

            val vehiculoViewModel: VehiculoViewModel = viewModel(
                factory = VehiculoViewModelFactory(aplicacion.vehiculoRepository)
            )

            PantallaConfiguracion(
                onVolver = { navController.popBackStack() },
                onAgregarVehiculo = { navController.navigate(Rutas.VEHICULO) },
                onEditarVehiculo = { vehiculo ->
                    navController.navigate(Rutas.editarVehiculo(vehiculo.id))
                },
                vehiculoViewModel = vehiculoViewModel
            )
        }

        composable(Rutas.VEHICULO) {
            val contexto = LocalContext.current.applicationContext
            val aplicacion = contexto as AplicacionAutoCare

            val vehiculoViewModel: VehiculoViewModel = viewModel(
                factory = VehiculoViewModelFactory(aplicacion.vehiculoRepository)
            )

            VehicleRegisterScreen(
                onVolver = { navController.popBackStack() },
                vehiculoViewModel = vehiculoViewModel,
                vehiculoEditar = null
            )
        }

        composable(Rutas.EDITAR_VEHICULO) { entrada ->
            val vehiculoId = entrada.arguments?.getString("vehiculoId")?.toIntOrNull()

            val contexto = LocalContext.current.applicationContext
            val aplicacion = contexto as AplicacionAutoCare

            val vehiculoViewModel: VehiculoViewModel = viewModel(
                factory = VehiculoViewModelFactory(aplicacion.vehiculoRepository)
            )

            var vehiculoEditar by remember(vehiculoId) { mutableStateOf<com.example.autocare.data.Vehiculo?>(null) }

            LaunchedEffect(vehiculoId) {
                vehiculoEditar = vehiculoId?.let { id ->
                    vehiculoViewModel.obtenerVehiculoPorId(id)
                }
            }

            if (vehiculoEditar != null) {
                VehicleRegisterScreen(
                    onVolver = { navController.popBackStack() },
                    vehiculoViewModel = vehiculoViewModel,
                    vehiculoEditar = vehiculoEditar
                )
            }
        }
    }
}