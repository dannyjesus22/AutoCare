
package com.example.autocare.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autocare.data.Vehiculo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaKilometraje(
    onVolver: () -> Unit,
    vehiculos: List<Vehiculo> = emptyList(),
    onGuardarKilometraje: (Int, Int) -> Unit = { _, _ -> }
) {

    // Vehículo seleccionado por el usuario
    var vehiculoSeleccionado by remember {
        mutableStateOf<Vehiculo?>(null)
    }

    // Kilómetros recorridos durante el día
    var kilometrosRecorridos by remember {
        mutableStateOf("")
    }

    // Controla el menú de selección
    var menuExpandido by remember {
        mutableStateOf(false)
    }

    // Mensaje de validación
    var mensajeError by remember {
        mutableStateOf("")
    }

    // Si solo existe un vehículo, lo seleccionamos automáticamente
    LaunchedEffect(vehiculos) {
        if (vehiculoSeleccionado == null && vehiculos.size == 1) {
            vehiculoSeleccionado = vehiculos.first()
        }
    }

    val kilometrosNuevos = kilometrosRecorridos.toIntOrNull()

    val kilometrajeActual = vehiculoSeleccionado?.kilometraje ?: 0

    val nuevoKilometraje = kilometrosNuevos?.let {
        if (it >= 0) {
            kilometrajeActual.toLong() + it.toLong()
        } else {
            null
        }
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
            text = "Registrar kilometraje",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D47A1)
        )

        Text(
            text = "Registra los kilómetros recorridos por tu vehículo",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Seleccionar vehículo
        Text(
            text = "Selecciona tu vehículo",
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = menuExpandido,
            onExpandedChange = {
                if (vehiculos.isNotEmpty()) {
                    menuExpandido = !menuExpandido
                }
            }
        ) {

            OutlinedTextField(
                value = vehiculoSeleccionado?.let {
                    "${it.marca} ${it.modelo} ${it.anio}"
                } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Vehículo") },
                placeholder = {
                    Text("Selecciona un vehículo")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = menuExpandido
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        ExposedDropdownMenuAnchorType.PrimaryNotEditable
                    )
            )

            ExposedDropdownMenu(
                expanded = menuExpandido,
                onDismissRequest = {
                    menuExpandido = false
                }
            ) {
                vehiculos.forEach { vehiculo ->

                    DropdownMenuItem(
                        text = {
                            Text(
                                "${vehiculo.marca} ${vehiculo.modelo} ${vehiculo.anio}"
                            )
                        },
                        onClick = {
                            vehiculoSeleccionado = vehiculo
                            kilometrosRecorridos = ""
                            mensajeError = ""
                            menuExpandido = false
                        }
                    )
                }
            }
        }

        if (vehiculos.isEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Primero debes registrar un vehículo en Configuración.",
                color = Color(0xFFB71C1C)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Información del vehículo seleccionado
        vehiculoSeleccionado?.let { vehiculo ->

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "${vehiculo.marca} ${vehiculo.modelo} ${vehiculo.anio}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )

                    Text(
                        text = vehiculo.tipoVehiculo,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Kilometraje actual: ${vehiculo.kilometraje} km",
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = kilometrosRecorridos,
            onValueChange = {
                kilometrosRecorridos = it.filter { caracter ->
                    caracter.isDigit()
                }
                mensajeError = ""
            },
            label = {
                Text("Kilómetros recorridos hoy")
            },
            placeholder = {
                Text("Ej: 42")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = vehiculoSeleccionado != null
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Vista previa del nuevo kilometraje
        if (nuevoKilometraje != null &&
            vehiculoSeleccionado != null
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9)
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "Nuevo kilometraje",
                        fontSize = 15.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = "$nuevoKilometraje km",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Calculado automáticamente",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        if (mensajeError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = mensajeError,
                color = Color.Red
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {

                val vehiculo = vehiculoSeleccionado
                val recorridos = kilometrosNuevos

                when {
                    vehiculo == null -> {
                        mensajeError = "Selecciona un vehículo."
                    }

                    recorridos == null || recorridos <= 0 -> {
                        mensajeError = "Ingresa una cantidad válida mayor a cero."
                    }

                    nuevoKilometraje == null ||
                            nuevoKilometraje > Int.MAX_VALUE -> {
                        mensajeError = "El kilometraje supera el límite permitido."
                    }

                    else -> {
                        onGuardarKilometraje(
                            vehiculo.id,
                            nuevoKilometraje.toInt()
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            enabled = vehiculos.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0D47A1)
            )
        ) {
            Text(
                text = "Guardar kilometraje",
                fontSize = 17.sp
            )
        }
    }
}
