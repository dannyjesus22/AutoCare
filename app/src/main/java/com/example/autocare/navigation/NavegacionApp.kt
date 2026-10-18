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

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel

import com.example.autocare.AplicacionAutoCare
import com.example.autocare.data.entities.Mantenimiento
import com.example.autocare.data.entities.Vehiculo
import com.example.autocare.data.repository.ResultadoAutenticacion

import com.example.autocare.ui.screens.HomeScreen
import com.example.autocare.ui.screens.LoginScreen
import com.example.autocare.ui.screens.PantallaConfiguracion
import com.example.autocare.ui.screens.PantallaHistorial
import com.example.autocare.ui.screens.PantallaKilometraje
import com.example.autocare.ui.screens.PantallaMantenimiento
import com.example.autocare.ui.screens.RegisterScreen
import com.example.autocare.ui.screens.SplashScreen
import com.example.autocare.ui.screens.VehicleRegisterScreen
import com.example.autocare.utils.GeneradorReportePdf

import com.example.autocare.ui.viewmodel.HistorialUiState
import com.example.autocare.ui.viewmodel.HistorialViewModel

import com.example.autocare.ui.viewmodel.MantenimientoViewModel
import com.example.autocare.ui.viewmodel.RegistroKilometrajeViewModel
import com.example.autocare.ui.viewmodel.VehiculoViewModel
import com.example.autocare.ui.viewmodel.usuarios.UsuarioViewModel

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private data class SesionCargada(val usuarioId: Int?)

@Composable
fun NavegacionPrueba() {

    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val aplicacion =
        context.applicationContext as AplicacionAutoCare

    // Leer la sesión guardada.
    val estadoSesion by remember(aplicacion) {
        aplicacion.configuracionDataStore.usuarioActivoId
            .map { id -> SesionCargada(id) }
    }.collectAsState(initial = null)

    val sesionCargada = estadoSesion != null
    val usuarioActivoId = estadoSesion?.usuarioId

    // Selección compartida entre Inicio e Historial.
    var vehiculoSeleccionadoId by remember(usuarioActivoId) {
        mutableStateOf<Int?>(null)
    }

    // Obtener los vehículos del usuario activo.
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
                        popUpTo(Rutas.SPLASH) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onIrInicio = {
                    navController.navigate(Rutas.INICIO) {
                        popUpTo(Rutas.SPLASH) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // 2. LOGIN
        composable(Rutas.LOGIN) {
            val usuarioViewModel: UsuarioViewModel = hiltViewModel()
            LoginScreen(
                onIniciarSesion = { usuarioId ->
                    scope.launch {
                        aplicacion.configuracionDataStore.guardarUsuarioActivo(usuarioId)
                        navController.navigate(Rutas.INICIO) {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
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
            val usuarioViewModel: UsuarioViewModel = hiltViewModel()

            RegisterScreen(
                onIrLogin = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.REGISTRO) { inclusive = true }
                    }
                },
                usuarioViewModel = usuarioViewModel
            )
        }

        // 4. INICIO
        composable(Rutas.INICIO) {
            val vehiculos by vehiculosDelUsuario.collectAsStateWithLifecycle(initialValue = emptyList())

            val vehiculoIdActual = vehiculos.find {
                it.id == vehiculoSeleccionadoId
            }?.id ?: vehiculos.firstOrNull()?.id

            val mantenimientoViewModel: MantenimientoViewModel = hiltViewModel()

            val flujoMantenimientos = remember(vehiculoIdActual, usuarioActivoId) {
                if (usuarioActivoId != null && vehiculoIdActual != null) {
                    mantenimientoViewModel.obtenerMantenimientosPorVehiculo(vehiculoIdActual)
                } else {
                    flowOf(emptyList<Mantenimiento>())
                }
            }

            val mantenimientos by flujoMantenimientos.collectAsStateWithLifecycle(initialValue = emptyList())

            HomeScreen(
                onIrKilometraje = { navController.navigate(Rutas.KILOMETRAJE) },
                onIrMantenimiento = { navController.navigate(Rutas.MANTENIMIENTO) },
                onIrHistorial = { navController.navigate(Rutas.HISTORIAL) },
                onIrConfiguracion = { navController.navigate(Rutas.CONFIGURACION) },
                vehiculos = vehiculos,
                mantenimientos = mantenimientos,
                vehiculoSeleccionadoId = vehiculoIdActual,
                onSeleccionarVehiculo = { id ->
                    if (vehiculos.any { it.id == id }) {
                        vehiculoSeleccionadoId = id
                    }
                }
            )
        }

        // 5. REGISTRO DE KILOMETRAJE
        composable(Rutas.KILOMETRAJE) {
            val vehiculos by vehiculosDelUsuario.collectAsStateWithLifecycle(initialValue = emptyList())

            val kilometrajeViewModel: RegistroKilometrajeViewModel = hiltViewModel()

            val mantenimientoViewModel: MantenimientoViewModel = hiltViewModel()

            LaunchedEffect(usuarioActivoId) {
                kilometrajeViewModel.establecerUsuarioActivo(usuarioActivoId)
            }

            PantallaKilometraje(
                onVolver = { navController.popBackStack() },
                vehiculos = vehiculos,
                vehiculoSeleccionadoId = vehiculos.find {
                    it.id == vehiculoSeleccionadoId
                }?.id ?: vehiculos.firstOrNull()?.id,
                onGuardarKilometraje = { vehiculoId, kilometrajeActual ->
                    val esVehiculoPropio = vehiculos.any {
                        it.id == vehiculoId && it.usuarioId == usuarioActivoId
                    }

                    if (usuarioActivoId != null && esVehiculoPropio) {
                        kilometrajeViewModel.guardarKilometraje(
                            vehiculoId = vehiculoId,
                            kilometrajeActual = kilometrajeActual
                        )

                        mantenimientoViewModel.verificarNotificacionKilometraje(
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
            val vehiculos by vehiculosDelUsuario.collectAsStateWithLifecycle(initialValue = emptyList())

            val vehiculoId = vehiculos.find {
                it.id == vehiculoSeleccionadoId
            }?.id ?: vehiculos.firstOrNull()?.id

            val historialViewModel: HistorialViewModel = hiltViewModel()

            val mantenimientoViewModel: MantenimientoViewModel = hiltViewModel()

            LaunchedEffect(vehiculoId, usuarioActivoId) {
                historialViewModel.limpiarHistorial()

                val idUsuario = usuarioActivoId
                if (vehiculoId != null && idUsuario != null) {
                    historialViewModel.cargarHistorial(
                        vehiculoId = vehiculoId,
                        usuarioId = idUsuario
                    )
                }
            }

            val estadoHistorial by historialViewModel.uiState.collectAsStateWithLifecycle()

            val registros = when (val estado = estadoHistorial) {
                is HistorialUiState.Success -> estado.registros
                else -> emptyList()
            }

            val flujoMantenimientos = remember(vehiculoId, usuarioActivoId) {
                if (vehiculoId != null && usuarioActivoId != null) {
                    mantenimientoViewModel.obtenerMantenimientosPorVehiculo(vehiculoId)
                } else {
                    flowOf(emptyList<Mantenimiento>())
                }
            }

            val registrosMantenimiento by flujoMantenimientos.collectAsStateWithLifecycle(initialValue = emptyList())

            PantallaHistorial(
                onVolver = { navController.popBackStack() },
                vehiculos = vehiculos,
                registros = if (vehiculoId != null && usuarioActivoId != null) {
                    registros.filter { it.vehiculoId == vehiculoId }
                } else {
                    emptyList()
                },
                registrosMantenimiento = if (vehiculoId != null && usuarioActivoId != null) {
                    registrosMantenimiento.filter { it.vehiculoId == vehiculoId }
                } else {
                    emptyList()
                },
                vehiculoSeleccionadoId = vehiculoId,
                onSeleccionarVehiculo = { id ->
                    if (vehiculos.any { it.id == id }) {
                        vehiculoSeleccionadoId = id
                    }
                },
                estadoHistorial = estadoHistorial,
                onCambiarEstadoMantenimiento = { mantenimiento, estaCompletado, kilometrajeActual ->
                    mantenimientoViewModel.cambiarEstadoMantenimiento(
                        mantenimiento = mantenimiento,
                        estaCompletado = estaCompletado,
                        kilometrajeActualVehiculo = kilometrajeActual
                    )
                },
                onGenerarPdf = {
                    val vehiculoActual = vehiculos.find { it.id == vehiculoId }
                    if (vehiculoActual != null) {
                        GeneradorReportePdf.generarYMostrarReporte(
                            context = context,
                            vehiculo = vehiculoActual,
                            registrosKilometraje = registros.filter { it.vehiculoId == vehiculoId },
                            mantenimientos = registrosMantenimiento.filter { it.vehiculoId == vehiculoId }
                        )
                    } else {
                        Toast.makeText(
                            context,
                            "Selecciona un vehículo primero para exportar el reporte",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }

        // 7. MANTENIMIENTO
        composable(Rutas.MANTENIMIENTO) {
            val vehiculos by vehiculosDelUsuario.collectAsStateWithLifecycle(initialValue = emptyList())

            val mantenimientoViewModel: MantenimientoViewModel = hiltViewModel()

            PantallaMantenimiento(
                vehiculos = if (usuarioActivoId != null) vehiculos else emptyList(),
                viewModel = mantenimientoViewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        // 8. CONFIGURACIÓN
        composable(Rutas.CONFIGURACION) {
            val vehiculoViewModel: VehiculoViewModel = hiltViewModel()

            LaunchedEffect(usuarioActivoId) {
                vehiculoViewModel.establecerUsuarioActivo(usuarioActivoId)
            }

            PantallaConfiguracion(
                onVolver = { navController.popBackStack() },
                onAgregarVehiculo = {
                    if (usuarioActivoId != null) {
                        navController.navigate(Rutas.VEHICULO)
                    }
                },
                onEditarVehiculo = { vehiculo ->
                    if (usuarioActivoId != null && vehiculo.usuarioId == usuarioActivoId) {
                        navController.navigate(Rutas.editarVehiculo(vehiculo.id))
                    }
                },
                onCambiarContrasena = { actual, nueva, confirmacion ->
                    val id = usuarioActivoId
                    if (id == null) {
                        Toast.makeText(context, "Debes iniciar sesión.", Toast.LENGTH_SHORT).show()
                        false
                    } else {
                        var esExitoso = false
                        scope.launch {
                            val resultado = aplicacion.usuarioRepository.cambiarContrasena(
                                usuarioId = id,
                                contrasenaActual = actual,
                                nuevaContrasena = nueva,
                                confirmarContrasena = confirmacion
                            )
                            when (resultado) {
                                is ResultadoAutenticacion.Exito -> {
                                    Toast.makeText(context, "Contraseña actualizada.", Toast.LENGTH_LONG).show()
                                    esExitoso = true
                                }
                                is ResultadoAutenticacion.Error -> {
                                    Toast.makeText(context, resultado.mensaje, Toast.LENGTH_LONG).show()
                                    esExitoso = false
                                }
                            }
                        }
                        esExitoso
                    }
                },
                onCerrarSesion = {
                    scope.launch {
                        aplicacion.configuracionDataStore.cerrarSesion()
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(Rutas.INICIO) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                vehiculoViewModel = vehiculoViewModel
            )
        }

        // 9. REGISTRAR VEHÍCULO
        composable(Rutas.VEHICULO) {
            val vehiculoViewModel: VehiculoViewModel = hiltViewModel()

            LaunchedEffect(usuarioActivoId) {
                vehiculoViewModel.establecerUsuarioActivo(usuarioActivoId)
            }

            if (usuarioActivoId != null) {
                VehicleRegisterScreen(
                    onVolver = { navController.popBackStack() },
                    vehiculoViewModel = vehiculoViewModel,
                    vehiculoEditar = null
                )
            }
        }

        // 10. EDITAR VEHÍCULO
        composable(Rutas.EDITAR_VEHICULO) { entrada ->
            val vehiculoId = entrada.arguments?.getString("vehiculoId")?.toIntOrNull()

            val vehiculoViewModel: VehiculoViewModel = hiltViewModel()

            LaunchedEffect(usuarioActivoId) {
                vehiculoViewModel.establecerUsuarioActivo(usuarioActivoId)
            }

            var vehiculoEditar by remember(vehiculoId, usuarioActivoId) {
                mutableStateOf<Vehiculo?>(null)
            }

            LaunchedEffect(vehiculoId, usuarioActivoId) {
                vehiculoViewModel.establecerUsuarioActivo(usuarioActivoId)

                vehiculoEditar = if (vehiculoId != null && usuarioActivoId != null) {
                    vehiculoViewModel.obtenerVehiculoPorId(vehiculoId)
                } else {
                    null
                }
            }

            if (usuarioActivoId != null && vehiculoEditar?.usuarioId == usuarioActivoId) {
                VehicleRegisterScreen(
                    onVolver = { navController.popBackStack() },
                    vehiculoViewModel = vehiculoViewModel,
                    vehiculoEditar = vehiculoEditar
                )
            }
        }
    }
}