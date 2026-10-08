
package com.example.autocare.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autocare.data.Vehiculo
import com.example.autocare.viewmodel.VehiculoViewModel
import java.util.Calendar

@Composable
fun VehicleRegisterScreen(
    onVolver: () -> Unit,
    vehiculoViewModel: VehiculoViewModel,
    vehiculoEditar: Vehiculo? = null
) {

    // Si vehiculoEditar es null, estamos registrando.
    // Si contiene un vehículo, estamos editándolo.
    val modoEdicion = vehiculoEditar != null

    var marca by remember(vehiculoEditar) {
        mutableStateOf(vehiculoEditar?.marca ?: "")
    }

    var modelo by remember(vehiculoEditar) {
        mutableStateOf(vehiculoEditar?.modelo ?: "")
    }

    var anio by remember(vehiculoEditar) {
        mutableStateOf(vehiculoEditar?.anio?.toString() ?: "")
    }

    var kilometraje by remember(vehiculoEditar) {
        mutableStateOf(vehiculoEditar?.kilometraje?.toString() ?: "")
    }

    var tipoVehiculo by remember(vehiculoEditar) {
        mutableStateOf(vehiculoEditar?.tipoVehiculo ?: "Inyección")
    }

    var mensajeError by remember(vehiculoEditar) {
        mutableStateOf("")
    }

    val tipos = listOf(
        "Carburador",
        "Inyección",
        "Eléctrico"
    )

    val anioActual = Calendar.getInstance().get(Calendar.YEAR)

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
            text = if (modoEdicion) "Editar vehículo" else "Registrar vehículo",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D47A1)
        )

        Text(
            text = if (modoEdicion)
                "Modifica los datos de tu vehículo"
            else
                "Ingresa los datos de tu vehículo",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = marca,
            onValueChange = { marca = it },
            label = { Text("Marca") },
            placeholder = { Text("Ej: Chevrolet") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = modelo,
            onValueChange = { modelo = it },
            label = { Text("Modelo") },
            placeholder = { Text("Ej: Corsa Evolution") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = anio,
            onValueChange = { anio = it },
            label = { Text("Año") },
            placeholder = { Text("Ej: 2007") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = kilometraje,
            onValueChange = { kilometraje = it },
            label = { Text("Kilometraje actual") },
            placeholder = { Text("Ej: 185462") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Tipo de vehículo",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        tipos.forEach { tipo ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = tipoVehiculo == tipo,
                    onClick = { tipoVehiculo = tipo }
                )

                Text(text = tipo)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (mensajeError.isNotEmpty()) {
            Text(
                text = mensajeError,
                color = Color.Red,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        Button(
            onClick = {

                val anioNumero = anio.toIntOrNull()
                val kilometrajeNumero = kilometraje.toIntOrNull()

                when {
                    marca.isBlank() || modelo.isBlank() ||
                            anio.isBlank() || kilometraje.isBlank() -> {
                        mensajeError = "Completa todos los campos."
                    }

                    anioNumero == null ||
                            anioNumero < 1886 ||
                            anioNumero > anioActual + 1 -> {
                        mensajeError = "Ingresa un año válido."
                    }

                    kilometrajeNumero == null ||
                            kilometrajeNumero < 0 -> {
                        mensajeError = "Ingresa un kilometraje válido."
                    }

                    else -> {
                        mensajeError = ""

                        if (vehiculoEditar != null) {

                            // Conservar el ID del vehículo original
                            val vehiculoActualizado = vehiculoEditar.copy(
                                marca = marca.trim(),
                                modelo = modelo.trim(),
                                anio = anioNumero,
                                kilometraje = kilometrajeNumero,
                                tipoVehiculo = tipoVehiculo
                            )

                            vehiculoViewModel.actualizarVehiculo(
                                vehiculoActualizado
                            )

                        } else {

                            // Crear un vehículo nuevo
                            val nuevoVehiculo = Vehiculo(
                                marca = marca.trim(),
                                modelo = modelo.trim(),
                                anio = anioNumero,
                                kilometraje = kilometrajeNumero,
                                tipoVehiculo = tipoVehiculo
                            )

                            vehiculoViewModel.guardarVehiculo(
                                nuevoVehiculo
                            )
                        }

                        onVolver()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0D47A1)
            )
        ) {
            Text(
                text = if (modoEdicion)
                    "Guardar cambios"
                else
                    "Guardar vehículo",
                fontSize = 17.sp
            )
        }
    }
}
