package com.example.autocare.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autocare.data.entities.Mantenimiento
import com.example.autocare.data.entities.RegistroKilometraje
import com.example.autocare.data.entities.Vehiculo
import com.example.autocare.ui.viewmodel.HistorialUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHistorial(
    onVolver: () -> Unit,
    vehiculos: List<Vehiculo>,
    registros: List<RegistroKilometraje>,
    registrosMantenimiento: List<Mantenimiento> = emptyList(),
    vehiculoSeleccionadoId: Int?,
    onSeleccionarVehiculo: (Int) -> Unit,
    estadoHistorial: HistorialUiState,
    onCambiarEstadoMantenimiento: (Mantenimiento, Boolean, Int) -> Unit = { _, _, _ -> },
    onGenerarPdf: () -> Unit = {}
) {
    var pestanaSeleccionada by remember { mutableIntStateOf(0) }

    val vehiculoSeleccionado = vehiculos.find {
        it.id == vehiculoSeleccionadoId
    } ?: vehiculos.firstOrNull()

    var menuAbierto by remember { mutableStateOf(false) }

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
            text = "Historial",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D47A1)
        )

        Text(
            text = "Consulta la actividad de tu vehículo",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (vehiculos.isEmpty()) {
            Text(
                text = "No tienes vehículos registrados.",
                color = Color.Gray
            )
        } else {
            ExposedDropdownMenuBox(
                expanded = menuAbierto,
                onExpandedChange = { menuAbierto = !menuAbierto }
            ) {
                OutlinedTextField(
                    value = vehiculoSeleccionado?.let { "${it.marca} ${it.modelo}" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Seleccionar vehículo") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuAbierto) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                )

                ExposedDropdownMenu(
                    expanded = menuAbierto,
                    onDismissRequest = { menuAbierto = false }
                ) {
                    vehiculos.forEach { vehiculo ->
                        DropdownMenuItem(
                            text = { Text("${vehiculo.marca} ${vehiculo.modelo} ${vehiculo.anio}") },
                            onClick = {
                                onSeleccionarVehiculo(vehiculo.id)
                                menuAbierto = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = vehiculoSeleccionado?.let { "${it.marca} ${it.modelo} ${it.anio}" } ?: "Vehículo no seleccionado",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Kilometraje actual: ${vehiculoSeleccionado?.kilometraje ?: 0} km"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            TabRow(selectedTabIndex = pestanaSeleccionada) {
                Tab(
                    selected = pestanaSeleccionada == 0,
                    onClick = { pestanaSeleccionada = 0 },
                    text = { Text("Kilometraje") }
                )
                Tab(
                    selected = pestanaSeleccionada == 1,
                    onClick = { pestanaSeleccionada = 1 },
                    text = { Text("Mantenimiento") }
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            when (pestanaSeleccionada) {
                0 -> {
                    when (val estado = estadoHistorial) {
                        is HistorialUiState.Loading -> CircularProgressIndicator()
                        is HistorialUiState.Success -> {
                            val registrosDelVehiculo = estado.registros.filter {
                                it.vehiculoId == vehiculoSeleccionado?.id
                            }
                            SeccionHistorialKilometraje(registros = registrosDelVehiculo)
                        }
                        is HistorialUiState.Error -> {
                            Text(
                                text = estado.mensaje,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                1 -> {
                    val mantenimientosDelVehiculo = registrosMantenimiento.filter {
                        it.vehiculoId == vehiculoSeleccionado?.id
                    }
                    SeccionHistorialMantenimiento(
                        mantenimientos = mantenimientosDelVehiculo,
                        kilometrajeActualVehiculo = vehiculoSeleccionado?.kilometraje ?: 0,
                        onCambiarEstadoMantenimiento = onCambiarEstadoMantenimiento
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onGenerarPdf,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                enabled = true,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1))
            ) {
                Text(text = "Generar reporte PDF", fontSize = 17.sp)
            }
        }
    }
}

@Composable
fun SeccionHistorialKilometraje(registros: List<RegistroKilometraje>) {
    if (registros.isEmpty()) {
        Text("No hay registros de kilometraje.", color = Color.Gray)
    } else {
        val registrosOrdenados = remember(registros) { registros.sortedByDescending { it.id } }
        val formatoFechaHora = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            registrosOrdenados.forEachIndexed { index, reg ->
                val regAnterior = registrosOrdenados.getOrNull(index + 1)

                // Si existe un registro anterior en la lista, calcula la diferencia.
                // Si no existe (es el primer registro), deduce los km ingresados restando la base de configuración.
                val recorrido = if (regAnterior != null) {
                    reg.kilometrajeActual - regAnterior.kilometrajeActual
                } else {
                    val residuo10000 = reg.kilometrajeActual % 10000
                    if (residuo10000 > 0) {
                        residuo10000
                    } else {
                        val residuo1000 = reg.kilometrajeActual % 1000
                        if (residuo1000 > 0) residuo1000 else reg.kilometrajeActual
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = formatoFechaHora.format(Date(reg.fecha)),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        if (recorrido > 0) {
                            Text(
                                text = "Recorrido: $recorrido km",
                                fontSize = 13.sp,
                                color = Color.DarkGray
                            )
                        }

                        Text(
                            text = "Kilometraje total: ${reg.kilometrajeActual} km",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SeccionHistorialMantenimiento(
    mantenimientos: List<Mantenimiento>,
    kilometrajeActualVehiculo: Int,
    onCambiarEstadoMantenimiento: (Mantenimiento, Boolean, Int) -> Unit
) {
    var mantenimientoAReabrir by remember { mutableStateOf<Mantenimiento?>(null) }

    if (mantenimientos.isEmpty()) {
        Text(
            text = "No hay registros de mantenimiento para este vehículo.",
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 12.dp)
        )
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            mantenimientos.forEach { mantenimiento ->
                val partesNotas = mantenimiento.notas.split(" - ", limit = 2)
                val taller = if (partesNotas.size > 1) partesNotas[0] else ""
                val observacion = if (partesNotas.size > 1) partesNotas[1] else mantenimiento.notas

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEDF4FC))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = mantenimiento.titulo,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF002B66)
                            )

                            Surface(
                                color = if (mantenimiento.completado) Color(0xFF2E7D32) else Color(0xFFE65100),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (mantenimiento.completado) "REALIZADO" else "PENDIENTE",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "📅 Fecha: ${mantenimiento.fecha}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0066FF)
                        )

                        Text(
                            text = "📍 Registrado inicialmente a los: ${mantenimiento.kilometrajeRealizado} km",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )

                        if (mantenimiento.completado) {
                            val kmCompletadoMostrar = mantenimiento.kilometrajeCompletado ?: mantenimiento.kilometrajeRealizado
                            Text(
                                text = "✓ Realizado a los: $kmCompletadoMostrar km",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }

                        mantenimiento.proximoKilometraje?.let { proxKm ->
                            Text(
                                text = "🔔 Notificar a los: $proxKm km",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFD84315)
                            )
                        }

                        if (mantenimiento.costo > 0) {
                            Text(
                                text = "Costo: $${mantenimiento.costo}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2E7D32)
                            )
                        }

                        if (taller.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "🏭 Taller: $taller",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF002B66)
                            )
                        }

                        if (observacion.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "📝 Observación: $observacion",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (!mantenimiento.completado) {
                            Button(
                                onClick = {
                                    onCambiarEstadoMantenimiento(
                                        mantenimiento,
                                        true,
                                        kilometrajeActualVehiculo
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("✓ Marcar como Realizado", fontSize = 13.sp)
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    mantenimientoAReabrir = mantenimiento
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Reabrir Mantenimiento", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para reabrir mantenimiento
    mantenimientoAReabrir?.let { mant ->
        AlertDialog(
            onDismissRequest = { mantenimientoAReabrir = null },
            title = {
                Text("Reabrir mantenimiento")
            },
            text = {
                Text("¿Estás seguro de que deseas reabrir el mantenimiento \"${mant.titulo}\"? Su estado cambiará a PENDIENTE.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onCambiarEstadoMantenimiento(
                            mant,
                            false,
                            mant.kilometrajeRealizado
                        )
                        mantenimientoAReabrir = null
                    }
                ) {
                    Text("Sí, reabrir", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mantenimientoAReabrir = null }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}