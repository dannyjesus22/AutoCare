
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

// STATEFUL: administra los estados y valida el formulario.
@Composable
fun PantallaKilometraje(
    onVolver: () -> Unit,
    vehiculos: List<Vehiculo> = emptyList(),
    onGuardarKilometraje: (Int, Int) -> Unit = { _, _ -> }
) {

    // Guardamos el ID del vehículo seleccionado.
    var vehiculoSeleccionadoId by remember {
        mutableStateOf<Int?>(null)
    }

    // Obtenemos el vehículo actualizado de la lista.
    val vehiculoSeleccionado = vehiculos.find {
        it.id == vehiculoSeleccionadoId
    }

    // Kilómetros recorridos durante el día.
    var kilometrosRecorridos by remember {
        mutableStateOf("")
    }

    // Mensaje para las validaciones.
    var mensajeError by remember {
        mutableStateOf("")
    }

    // Seleccionar automáticamente si existe un solo vehículo.
    LaunchedEffect(vehiculos) {
        if (
            vehiculos.size == 1 &&
            vehiculoSeleccionadoId == null
        ) {
            vehiculoSeleccionadoId = vehiculos.first().id
        }

        // Limpiar la selección si el vehículo ya no existe.
        if (
            vehiculoSeleccionadoId != null &&
            vehiculos.none { it.id == vehiculoSeleccionadoId }
        ) {
            vehiculoSeleccionadoId = null
        }
    }

    // Convertir el texto a número.
    val kilometrosNuevos = kilometrosRecorridos.toIntOrNull()

    // Kilometraje guardado actualmente.
    val kilometrajeActual = vehiculoSeleccionado?.kilometraje ?: 0

    // Calcular el nuevo kilometraje sin desbordar Int.
    val nuevoKilometraje = kilometrosNuevos?.let {
        if (it >= 0) {
            kilometrajeActual.toLong() + it.toLong()
        } else {
            null
        }
    }

    // STATE HOISTING:
    // Enviamos datos y funciones al componente Stateless.
    ContenidoKilometraje(
        onVolver = onVolver,
        vehiculos = vehiculos,
        vehiculoSeleccionadoId = vehiculoSeleccionadoId,
        onSeleccionarVehiculo = { id ->
            vehiculoSeleccionadoId = id
            kilometrosRecorridos = ""
            mensajeError = ""
        },
        vehiculoSeleccionado = vehiculoSeleccionado,
        kilometrosRecorridos = kilometrosRecorridos,
        onKilometrosChange = { texto ->
            kilometrosRecorridos = texto.filter { it.isDigit() }
            mensajeError = ""
        },
        nuevoKilometraje = nuevoKilometraje,
        mensajeError = mensajeError,
        onGuardar = {
            val vehiculo = vehiculoSeleccionado
            val recorridos = kilometrosNuevos

            when {
                vehiculo == null -> {
                    mensajeError = "Selecciona un vehículo."
                }

                recorridos == null || recorridos <= 0 -> {
                    mensajeError =
                        "Ingresa una cantidad válida mayor a cero."
                }

                nuevoKilometraje == null ||
                        nuevoKilometraje > Int.MAX_VALUE -> {
                    mensajeError =
                        "El kilometraje supera el límite permitido."
                }

                else -> {
                    onGuardarKilometraje(
                        vehiculo.id,
                        nuevoKilometraje.toInt()
                    )
                }
            }
        }
    )
}


// STATELESS: recibe los datos y muestra la interfaz.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenidoKilometraje(
    onVolver: () -> Unit,
    vehiculos: List<Vehiculo>,
    vehiculoSeleccionadoId: Int?,
    onSeleccionarVehiculo: (Int) -> Unit,
    vehiculoSeleccionado: Vehiculo?,
    kilometrosRecorridos: String,
    onKilometrosChange: (String) -> Unit,
    nuevoKilometraje: Long?,
    mensajeError: String,
    onGuardar: () -> Unit
) {

    // Estado visual local: solo controla si el menú está abierto.
    var menuExpandido by remember {
        mutableStateOf(false)
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
                            onSeleccionarVehiculo(vehiculo.id)
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

        // Tarjeta con la información del vehículo.
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
            onValueChange = onKilometrosChange,
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

        // Vista previa del kilometraje calculado.
        if (
            nuevoKilometraje != null &&
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
            onClick = onGuardar,
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
