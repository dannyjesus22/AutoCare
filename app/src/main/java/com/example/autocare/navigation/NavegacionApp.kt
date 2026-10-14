
package com.example.autocare.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.autocare.viewmodel.usuarios.UsuarioViewModel
import com.example.autocare.viewmodel.usuarios.UsuarioViewModelFactory

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
fun NavegacionPrueba() {

    val navController = rememberNavController()

    val scope = rememberCoroutineScope()

    val aplicacion = LocalContext.current.applicationContext
            as AplicacionAutoCare

    // Consultar el usuario conectado.
    val usuarioActivoId by aplicacion.configuracionDataStore
        .usuarioActivoId
        .collectAsStateWithLifecycle(initialValue = null)

    // Consultar solamente los vehículos del usuario activo.
    val vehiculosDelUsuario = remember(
        aplicacion,
        usuarioActivoId
    ) {

        val id = usuarioActivoId

        if (id != null) {

            aplicacion.vehiculoRepository
                .obtenerVehiculosPorUsuario(id)

        } else {

            flowOf(emptyList<Vehiculo>())
        }
    }

    NavHost(
        navController = navController,
        startDestination = Rutas.SPLASH
    ) {

        // 1. SPLASH
        composable(Rutas.SPLASH) {

            SplashScreen(
                onIrLogin = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.SPLASH) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // 2. LOGIN
        composable(Rutas.LOGIN) {

            val usuarioViewModel: UsuarioViewModel = viewModel(
                factory = UsuarioViewModelFactory(
                    aplicacion.usuarioRepository
                )
            )

            LoginScreen(
                onIniciarSesion = { usuarioId ->

                    scope.launch {

                        // Guardar el usuario autenticado.
                        aplicacion.configuracionDataStore
                            .guardarUsuarioActivo(usuarioId)

                        // Entrar al inicio y quitar Login del historial.
                        navController.navigate(Rutas.INICIO) {
                            popUpTo(Rutas.LOGIN) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                },
                onIrRegistro = {
                    navController.navigate(Rutas.REGISTRO)
                },
                usuarioViewModel = usuarioViewModel
            )
        }

        // 3. REGISTRO
        composable(Rutas.REGISTRO) {

            val usuarioViewModel: UsuarioViewModel = viewModel(
                factory = UsuarioViewModelFactory(
                    aplicacion.usuarioRepository
                )
            )

            RegisterScreen(
                onIrLogin = {

                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.REGISTRO) {
                            inclusive = true
                        }
                    }
                },
                usuarioViewModel = usuarioViewModel
            )
        }

        // 4. INICIO
        composable(Rutas.INICIO) {

            val vehiculos by vehiculosDelUsuario
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

        // 5. KILOMETRAJE
        composable(Rutas.KILOMETRAJE) {

            val vehiculos by vehiculosDelUsuario
                .collectAsStateWithLifecycle(
                    initialValue = emptyList()
                )

            val kilometrajeViewModel: RegistroKilometrajeViewModel =
                viewModel(
                    factory = RegistroKilometrajeViewModelFactory(
                        aplicacion.registroKilometrajeRepository
                    )
                )

            LaunchedEffect(usuarioActivoId) {
                kilometrajeViewModel.establecerUsuarioActivo(
                    usuarioActivoId
                )
            }

            PantallaKilometraje(
                onVolver = {
                    navController.popBackStack()
                },
                vehiculos = vehiculos,
                onGuardarKilometraje = { vehiculoId, kilometrajeActual ->

                    val esVehiculoPropio = vehiculos.any {
                        it.id == vehiculoId
                    }

                    if (
                        usuarioActivoId != null &&
                        esVehiculoPropio
                    ) {

                        kilometrajeViewModel.guardarKilometraje(
                            vehiculoId = vehiculoId,
                            kilometrajeActual = kilometrajeActual
                        )
                    }
                }
            )
        }

        // 6. HISTORIAL
        composable(Rutas.HISTORIAL) {

            val vehiculos by vehiculosDelUsuario
                .collectAsStateWithLifecycle(
                    initialValue = emptyList()
                )

            var vehiculoSeleccionadoId by remember {
                mutableStateOf<Int?>(null)
            }

            val vehiculoId = vehiculos.find {
                it.id == vehiculoSeleccionadoId
            }?.id ?: vehiculos.firstOrNull()?.id

            val historialViewModel: HistorialViewModel = viewModel(
                factory = HistorialViewModelFactory(
                    aplicacion.registroKilometrajeRepository
                )
            )

            LaunchedEffect(vehiculoId, usuarioActivoId) {

                // Limpiar los registros anteriores.
                historialViewModel.limpiarHistorial()

                val idUsuario = usuarioActivoId

                if (
                    vehiculoId != null &&
                    idUsuario != null
                ) {

                    historialViewModel.cargarHistorial(
                        vehiculoId = vehiculoId,
                        usuarioId = idUsuario
                    )
                }
            }

            val estadoHistorial by historialViewModel.uiState
                .collectAsStateWithLifecycle()

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
                registros = if (
                    vehiculoId != null &&
                    usuarioActivoId != null
                ) {
                    registros
                } else {
                    emptyList()
                },
                vehiculoSeleccionadoId = vehiculoId,
                onSeleccionarVehiculo = { id ->
                    vehiculoSeleccionadoId = id
                },
                estadoHistorial = estadoHistorial
            )
        }

        // 7. MANTENIMIENTO
        // Se conserva la pantalla de Maykol.
        composable(Rutas.MANTENIMIENTO) {

            PantallaMantenimiento(
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        // 8. CONFIGURACIÓN
        composable(Rutas.CONFIGURACION) {

            val vehiculoViewModel: VehiculoViewModel = viewModel(
                factory = VehiculoViewModelFactory(
                    aplicacion.vehiculoRepository
                )
            )

            LaunchedEffect(usuarioActivoId) {
                vehiculoViewModel.establecerUsuarioActivo(
                    usuarioActivoId
                )
            }

            PantallaConfiguracion(
                onVolver = {
                    navController.popBackStack()
                },
                onAgregarVehiculo = {

                    if (usuarioActivoId != null) {
                        navController.navigate(Rutas.VEHICULO)
                    }
                },
                onEditarVehiculo = { vehiculo ->

                    if (
                        usuarioActivoId != null &&
                        vehiculo.usuarioId == usuarioActivoId
                    ) {

                        navController.navigate(
                            Rutas.editarVehiculo(vehiculo.id)
                        )
                    }
                },

                // NUEVO: cerrar sesión sin borrar la base de datos.
                onCerrarSesion = {

                    scope.launch {

                        // Eliminar solamente el ID de sesión.
                        aplicacion.configuracionDataStore
                            .cerrarSesion()

                        // Regresar al Login y limpiar las pantallas.
                        navController.navigate(Rutas.LOGIN) {

                            popUpTo(Rutas.INICIO) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    }
                },

                vehiculoViewModel = vehiculoViewModel
            )
        }

        // 9. REGISTRAR VEHÍCULO
        composable(Rutas.VEHICULO) {

            val vehiculoViewModel: VehiculoViewModel = viewModel(
                factory = VehiculoViewModelFactory(
                    aplicacion.vehiculoRepository
                )
            )

            LaunchedEffect(usuarioActivoId) {
                vehiculoViewModel.establecerUsuarioActivo(
                    usuarioActivoId
                )
            }

            if (usuarioActivoId != null) {

                VehicleRegisterScreen(
                    onVolver = {
                        navController.popBackStack()
                    },
                    vehiculoViewModel = vehiculoViewModel,
                    vehiculoEditar = null
                )
            }
        }

        // 10. EDITAR VEHÍCULO
        composable(Rutas.EDITAR_VEHICULO) { entrada ->

            val vehiculoId = entrada.arguments
                ?.getString("vehiculoId")
                ?.toIntOrNull()

            val vehiculoViewModel: VehiculoViewModel = viewModel(
                factory = VehiculoViewModelFactory(
                    aplicacion.vehiculoRepository
                )
            )

            LaunchedEffect(usuarioActivoId) {
                vehiculoViewModel.establecerUsuarioActivo(
                    usuarioActivoId
                )
            }

            var vehiculoEditar by remember(
                vehiculoId,
                usuarioActivoId
            ) {
                mutableStateOf<Vehiculo?>(null)
            }

            LaunchedEffect(
                vehiculoId,
                usuarioActivoId
            ) {

                vehiculoViewModel.establecerUsuarioActivo(
                    usuarioActivoId
                )

                vehiculoEditar = if (
                    vehiculoId != null &&
                    usuarioActivoId != null
                ) {

                    vehiculoViewModel.obtenerVehiculoPorId(
                        vehiculoId
                    )

                } else {
                    null
                }
            }

            if (
                usuarioActivoId != null &&
                vehiculoEditar?.usuarioId == usuarioActivoId
            ) {

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
