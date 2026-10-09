package com.example.autocare.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autocare.viewmodel.MantenimientoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PantallaMantenimiento(
    vehiculoId: Int = 1, // Recibe el ID del vehículo actual (por defecto 1 para pruebas)
    kilometrajeActual: Int = 185462, // Recibe el kilometraje del vehículo
    viewModel: MantenimientoViewModel? = null, // ViewModel de mantenimiento
    onVolver: () -> Unit
) {
    val context = LocalContext.current

    var tipoMantenimiento by remember { mutableStateOf("") }
    var costo by remember { mutableStateOf("") }
    var taller by remember { mutableStateOf("") }
    var intervaloKilometros by remember { mutableStateOf("") }

    val intervaloNumero = intervaloKilometros.toIntOrNull() ?: 0
    val proximoMantenimiento = kilometrajeActual + intervaloNumero

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {

        // Regresar al inicio
        TextButton(
            onClick = { onVolver() }
        ) {
            Text("← Volver")
        }

        Text(
            text = "Registrar mantenimiento",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D47A1)
        )

        Text(
            text = "Guarda los mantenimientos de tu vehículo",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Chevrolet Corsa Evolution 2007",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D47A1)
                )

                Text(
                    text = "Kilometraje actual: $kilometrajeActual km",
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = tipoMantenimiento,
            onValueChange = { tipoMantenimiento = it },
            label = { Text("Tipo de mantenimiento") },
            placeholder = { Text("Ej: Cambio de aceite") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = costo,
            onValueChange = { costo = it },
            label = { Text("Costo ($)") },
            placeholder = { Text("Ej: 35") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = taller,
            onValueChange = { taller = it },
            label = { Text("Taller / Observaciones") },
            placeholder = { Text("Ej: Taller Automotriz Manta") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = intervaloKilometros,
            onValueChange = { intervaloKilometros = it },
            label = { Text("Recordar mantenimiento en (km)") },
            placeholder = { Text("Ej: 5000") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(15.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE8F5E9)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Próximo mantenimiento",
                    fontSize = 15.sp,
                    color = Color.Gray
                )

                Text(
                    text = "$proximoMantenimiento km",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val costoDouble = costo.toDoubleOrNull()

                if (tipoMantenimiento.isBlank()) {
                    Toast.makeText(context, "Por favor escribe el tipo de mantenimiento", Toast.LENGTH_SHORT).show()
                } else if (costoDouble == null) {
                    Toast.makeText(context, "Por favor ingresa un costo válido", Toast.LENGTH_SHORT).show()
                } else {
                    // Obtener fecha actual formato YYYY-MM-DD
                    val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                    // Guardar a través del ViewModel
                    viewModel?.guardarMantenimiento(
                        vehiculoId = vehiculoId,
                        titulo = tipoMantenimiento,
                        tipo = "General",
                        kilometrajeRealizado = kilometrajeActual,
                        proximoKilometraje = if (intervaloNumero > 0) proximoMantenimiento else null,
                        fecha = fechaActual,
                        proximaFecha = null,
                        costo = costoDouble,
                        notas = taller
                    )

                    Toast.makeText(context, "Mantenimiento guardado correctamente", Toast.LENGTH_SHORT).show()
                    onVolver()
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
                text = "Guardar mantenimiento",
                fontSize = 17.sp
            )
        }
    }
}