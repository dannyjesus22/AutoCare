package com.example.autocare.ui.screens

import androidx.activity.compose.BackHandler
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.autocare.data.entities.Vehiculo
import com.example.autocare.ui.viewmodel.ConfiguracionViewModel
import com.example.autocare.ui.viewmodel.VehiculoViewModel
import kotlinx.coroutines.launch

@Composable
fun PantallaConfiguracion(
    onVolver: () -> Unit,
    onAgregarVehiculo: () -> Unit,
    onEditarVehiculo: (Vehiculo) -> Unit,
    onCerrarSesion: () -> Unit,
    vehiculoViewModel: VehiculoViewModel,
    onCambiarContrasena: suspend (
        String,
        String,
        String
    ) -> Boolean
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var guardandoContrasena by remember { mutableStateOf(false) }

    val configViewModel: ConfiguracionViewModel = hiltViewModel()

    val notificacionesActivas by configViewModel
        .notificacionesActivadas
        .collectAsState()

    val recordatoriosActivos by configViewModel
        .recordatoriosActivados
        .collectAsState()

    val vehiculos by vehiculoViewModel.vehiculos.collectAsState()

    var vehiculoAEliminar by remember { mutableStateOf<Vehiculo?>(null) }
    var mostrarConfirmacionSalida by remember { mutableStateOf(false) }
    var mostrarCambioContrasena by remember { mutableStateOf(false) }

    var contrasenaActual by remember { mutableStateOf("") }
    var nuevaContrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }

    // Intercepta el botón de atrás físico del teléfono
    BackHandler {
        onVolver()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {

        TextButton(onClick = onVolver) {
            Text("← Volver")
        }

        Text(
            text = "Configuración",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D47A1)
        )

        Text(
            text = "Personaliza las opciones de AutoCare",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "Mis vehículos",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D47A1)
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (vehiculos.isEmpty()) {
            Text(
                text = "Todavía no tienes vehículos registrados.",
                color = Color.Gray
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                vehiculos.forEach { vehiculo ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE3F2FD)
                        )
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "${vehiculo.marca} ${vehiculo.modelo}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D47A1)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text("Año: ${vehiculo.anio}")
                            Text("Tipo: ${vehiculo.tipoVehiculo}")
                            Text("Kilometraje: ${vehiculo.kilometraje} km")

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { onEditarVehiculo(vehiculo) }
                                ) {
                                    Text("Editar")
                                }

                                OutlinedButton(
                                    onClick = { vehiculoAEliminar = vehiculo }
                                ) {
                                    Text(
                                        text = "Eliminar",
                                        color = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onAgregarVehiculo,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0D47A1)
            )
        ) {
            Text(
                text = "+ Agregar vehículo",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "Notificaciones y Preferencias",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D47A1)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Activar notificaciones",
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = notificacionesActivas,
                onCheckedChange = { activado ->
                    configViewModel.cambiarNotificaciones(activado)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF0D47A1)
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recordatorios de mantenimiento",
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = recordatoriosActivos,
                onCheckedChange = { activado ->
                    configViewModel.cambiarRecordatorios(activado)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF0D47A1)
                )
            )
        }

        Spacer(modifier = Modifier.height(35.dp))

        // SECCIÓN CUENTA
        Text(
            text = "Cuenta",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D47A1)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Administra la seguridad y sesión de tu cuenta.",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { mostrarCambioContrasena = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
        ) {
            Text(
                text = "Cambiar contraseña",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = { mostrarConfirmacionSalida = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC62828)
            )
        ) {
            Text(
                text = "Cerrar sesión",
                fontSize = 17.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Diálogo para cambiar contraseña
    if (mostrarCambioContrasena) {
        AlertDialog(
            onDismissRequest = { mostrarCambioContrasena = false },
            title = { Text("Cambiar contraseña") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = contrasenaActual,
                        onValueChange = { contrasenaActual = it },
                        label = { Text("Contraseña actual") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = nuevaContrasena,
                        onValueChange = { nuevaContrasena = it },
                        label = { Text("Nueva contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = confirmarContrasena,
                        onValueChange = { confirmarContrasena = it },
                        label = { Text("Confirmar contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !guardandoContrasena,
                    onClick = {
                        scope.launch {
                            guardandoContrasena = true
                            try {
                                val cambioExitoso = onCambiarContrasena(
                                    contrasenaActual,
                                    nuevaContrasena,
                                    confirmarContrasena
                                )

                                if (cambioExitoso) {
                                    mostrarCambioContrasena = false
                                    contrasenaActual = ""
                                    nuevaContrasena = ""
                                    confirmarContrasena = ""
                                }
                            } finally {
                                guardandoContrasena = false
                            }
                        }
                    }
                ) {
                    Text(if (guardandoContrasena) "Guardando..." else "Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCambioContrasena = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Confirmación para eliminar vehículo
    vehiculoAEliminar?.let { vehiculo ->
        AlertDialog(
            onDismissRequest = { vehiculoAEliminar = null },
            title = { Text("Eliminar vehículo") },
            text = {
                Text(
                    "¿Estás seguro de eliminar ${vehiculo.marca} ${vehiculo.modelo}? Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vehiculoViewModel.eliminarVehiculo(vehiculo)
                        vehiculoAEliminar = null
                    }
                ) {
                    Text(text = "Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { vehiculoAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Confirmación para cerrar sesión
    if (mostrarConfirmacionSalida) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacionSalida = false },
            title = { Text("Cerrar sesión") },
            text = {
                Text(
                    "¿Estás seguro de que deseas cerrar sesión? Tus vehículos y registros permanecerán guardados."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacionSalida = false
                        onCerrarSesion()
                    }
                ) {
                    Text(text = "Sí, cerrar sesión", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacionSalida = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}