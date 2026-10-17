package com.example.autocare.ui.screens

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
import com.example.autocare.data.entities.Vehiculo

// STATEFUL: administra el formulario y sus validaciones.
@Composable
fun PantallaKilometraje(
    onVolver: () -> Unit,
    vehiculos: List<Vehiculo> = emptyList(),
    onGuardarKilometraje: (Int, Int) -> Unit = { _, _ -> },
    vehiculoSeleccionadoId: Int? = null
) {

    // El vehículo se elige desde Inicio.
    val vehiculoSeleccionado = vehiculos.find {
        it.id == vehiculoSeleccionadoId
    }

    var kilometrosRecorridos by remember(
        vehiculoSeleccionadoId
    ) {
        mutableStateOf("")
    }

    var mensajeError by remember(
        vehiculoSeleccionadoId
    ) {
        mutableStateOf("")
    }

    // Convertir los kilómetros ingresados a número.
    val kilometrosNuevos = kilometrosRecorridos.toIntOrNull()

    // Kilometraje actual del vehículo.
    val kilometrajeActual =
        vehiculoSeleccionado?.kilometraje ?: 0

    // Sumar sin provocar desbordamiento de Int.
    val nuevoKilometraje = kilometrosNuevos?.let {
        if (it >= 0) {
            kilometrajeActual.toLong() + it.toLong()
        } else {
            null
        }
    }

    // STATE HOISTING: enviamos datos al componente Stateless.
    ContenidoKilometraje(
        onVolver = onVolver,
        vehiculoSeleccionado = vehiculoSeleccionado,
        kilometrosRecorridos = kilometrosRecorridos,

        onKilometrosChange = { texto ->
            kilometrosRecorridos = texto.filter {
                it.isDigit()
            }
            mensajeError = ""
        },

        nuevoKilometraje = nuevoKilometraje,
        mensajeError = mensajeError,

        onGuardar = {
            val vehiculo = vehiculoSeleccionado
            val recorridos = kilometrosNuevos

            when {
                vehiculo == null -> {
                    mensajeError =
                        "No hay un vehículo seleccionado."
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

                    // Limpiar el formulario después de guardar.
                    kilometrosRecorridos = ""
                    mensajeError = ""
                }
            }
        }
    )
}

// STATELESS: muestra la información que recibe.
@Composable
fun ContenidoKilometraje(
    onVolver: () -> Unit,
    vehiculoSeleccionado: Vehiculo?,
    kilometrosRecorridos: String,
    onKilometrosChange: (String) -> Unit,
    nuevoKilometraje: Long?,
    mensajeError: String,
    onGuardar: () -> Unit
) {

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
            text = "Vehículo seleccionado",
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Mostrar el vehículo sin menú desplegable.
        if (vehiculoSeleccionado != null) {

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
                        text = "${vehiculoSeleccionado.marca} " +
                                "${vehiculoSeleccionado.modelo} " +
                                "${vehiculoSeleccionado.anio}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = vehiculoSeleccionado.tipoVehiculo,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Kilometraje actual: " +
                                "${vehiculoSeleccionado.kilometraje} km",
                        fontSize = 16.sp
                    )
                }
            }

        } else {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Selecciona primero un vehículo desde Inicio.",
                    modifier = Modifier.padding(18.dp),
                    color = Color(0xFFB71C1C)
                )
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

        // Vista previa del kilometraje acumulado.
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
            enabled = vehiculoSeleccionado != null,
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
