
package com.example.autocare.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.autocare.data.Mantenimiento
import com.example.autocare.data.Vehiculo
import com.example.autocare.data.usuarios.ResultadoAutenticacion

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
import com.example.autocare.viewmodel.usuarios.UsuarioViewModel
import com.example.autocare.viewmodel.usuarios.UsuarioViewModelFactory

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// Permite saber cuándo DataStore terminó de leer la sesión.
private data class SesionCargada(val usuarioId: Int?)

@Composable
fun NavegacionPrueba() {

    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val aplicacion =
        context.applicationContext as AplicacionAutoCare

    // Leer la sesión guardada en DataStore.
    val estadoSesion by remember(aplicacion) {
        aplicacion.configuracionDataStore.usuarioActivoId
            .map { id -> SesionCargada(id) }
    }.collectAsState(initial = null)

    val sesionCargada = estadoSesion != null
    val usuarioActivoId = estadoSesion?.usuarioId

    // Obtener solo los vehículos del usuario activo.
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
                usuarioActivoId = usuarioActivoId,
                sesionCargada = sesionCargada,

                onIrLogin = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.SPLASH) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },

                onIrInicio = {
                    navController.navigate(Rutas.INICIO) {
                        popUpTo(Rutas.SPLASH) {
                            inclusive = true
                        }
                        launchSingleTop = true
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
                        aplicacion.configuracionDataStore
                            .guardarUsuarioActivo(usuarioId)

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

            val mantenimientoViewModel: MantenimientoViewModel =
                viewModel(
                    factory = MantenimientoViewModelFactory(
                        aplicacion.mantenimientoRepository
                    )
                )

            // Obtener mantenimientos de los vehículos propios.
            val idsVehiculos = vehiculos.map { it.id }

            val flujoMantenimientos = remember(
                idsVehiculos,
                usuarioActivoId
            ) {
                if (
                    usuarioActivoId != null &&
                    idsVehiculos.isNotEmpty()
                ) {
                    kotlinx.coroutines.flow.combine(
                        idsVehiculos.map { id ->
                            mantenimientoViewModel
                                .obtenerMantenimientosPorVehiculo(id)
                        }
                    ) { listas ->
                        listas.flatMap { it }
                    }
                } else {
                    flowOf(emptyList<Mantenimiento>())
                }
            }

            val mantenimientos by flujoMantenimientos
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

                vehiculos = vehiculos,
                mantenimientos = mantenimientos
            )
        }

        // 5. REGISTRO DE KILOMETRAJE
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

            val mantenimientoViewModel: MantenimientoViewModel =
                viewModel(
                    factory = MantenimientoViewModelFactory(
                        aplicacion.mantenimientoRepository
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

                onGuardarKilometraje = {
                        vehiculoId, kilometrajeActual ->

                    val esVehiculoPropio = vehiculos.any {
                        it.id == vehiculoId &&
                                it.usuarioId == usuarioActivoId
                    }

                    if (
                        usuarioActivoId != null &&
                        esVehiculoPropio
                    ) {

                        kilometrajeViewModel.guardarKilometraje(
                            vehiculoId = vehiculoId,
                            kilometrajeActual = kilometrajeActual
                        )

                        mantenimientoViewModel
                            .verificarNotificacionKilometraje(
                                vehiculoId = vehiculoId,
                                nuevoKilometraje = kilometrajeActual,
                                context = context
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

            val mantenimientoViewModel: MantenimientoViewModel =
                viewModel(
                    factory = MantenimientoViewModelFactory(
                        aplicacion.mantenimientoRepository
                    )
                )

            LaunchedEffect(vehiculoId, usuarioActivoId) {

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

            val registros = when (
                val estado = estadoHistorial
            ) {
                is HistorialUiState.Success -> estado.registros
                else -> emptyList()
            }

            // Mantenimientos del vehículo seleccionado.
            val flujoMantenimientos = remember(
                vehiculoId,
                usuarioActivoId
            ) {
                if (
                    vehiculoId != null &&
                    usuarioActivoId != null
                ) {
                    mantenimientoViewModel
                        .obtenerMantenimientosPorVehiculo(vehiculoId)
                } else {
                    flowOf(emptyList<Mantenimiento>())
                }
            }

            val registrosMantenimiento by flujoMantenimientos
                .collectAsStateWithLifecycle(
                    initialValue = emptyList()
                )

            PantallaHistorial(
                onVolver = {
                    navController.popBackStack()
                },

                vehiculos = vehiculos,

                registros = if (
                    vehiculoId != null &&
                    usuarioActivoId != null
                ) {
                    registros.filter {
                        it.vehiculoId == vehiculoId
                    }
                } else {
                    emptyList()
                },

                registrosMantenimiento = if (
                    vehiculoId != null &&
                    usuarioActivoId != null
                ) {
                    registrosMantenimiento.filter {
                        it.vehiculoId == vehiculoId
                    }
                } else {
                    emptyList()
                },

                vehiculoSeleccionadoId = vehiculoId,

                onSeleccionarVehiculo = { id ->
                    if (vehiculos.any { it.id == id }) {
                        vehiculoSeleccionadoId = id
                    }
                },

                estadoHistorial = estadoHistorial
            )
        }

        // 7. MANTENIMIENTO
        composable(Rutas.MANTENIMIENTO) {

            val vehiculos by vehiculosDelUsuario
                .collectAsStateWithLifecycle(
                    initialValue = emptyList()
                )

            val mantenimientoViewModel: MantenimientoViewModel =
                viewModel(
                    factory = MantenimientoViewModelFactory(
                        aplicacion.mantenimientoRepository
                    )
                )

            PantallaMantenimiento(
                vehiculos = if (usuarioActivoId != null) {
                    vehiculos
                } else {
                    emptyList()
                },

                viewModel = mantenimientoViewModel,

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


                onCambiarContrasena = { actual, nueva, confirmacion ->

                    val id = usuarioActivoId

                    if (id == null) {
                        Toast.makeText(
                            context,
                            "Debes iniciar sesión.",
                            Toast.LENGTH_SHORT
                        ).show()

                        false
                    } else {
                        val resultado = aplicacion.usuarioRepository
                            .cambiarContrasena(
                                usuarioId = id,
                                contrasenaActual = actual,
                                nuevaContrasena = nueva,
                                confirmarContrasena = confirmacion
                            )

                        when (resultado) {
                            is ResultadoAutenticacion.Exito -> {
                                Toast.makeText(
                                    context,
                                    "Contraseña actualizada correctamente.",
                                    Toast.LENGTH_LONG
                                ).show()

                                true
                            }

                            is ResultadoAutenticacion.Error -> {
                                Toast.makeText(
                                    context,
                                    resultado.mensaje,
                                    Toast.LENGTH_LONG
                                ).show()

                                false
                            }
                        }
                    }
                },

                onCerrarSesion = {

                    scope.launch {
                        aplicacion.configuracionDataStore
                            .cerrarSesion()

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
