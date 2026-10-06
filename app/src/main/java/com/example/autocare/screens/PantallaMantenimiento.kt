package com.example.autocare.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaMantenimiento(
    onVolver: () -> Unit
) {

    val kilometrajeActual = 185462

    var tipoMantenimiento by remember {
        mutableStateOf("")
    }

    var costo by remember {
        mutableStateOf("")
    }

    var taller by remember {
        mutableStateOf("")
    }

    var intervaloKilometros by remember {
        mutableStateOf("")
    }

    val intervaloNumero = intervaloKilometros.toIntOrNull() ?: 0

    val proximoMantenimiento = kilometrajeActual + intervaloNumero

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        // Regresar al inicio
        TextButton(
            onClick = {
                onVolver()
            }
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

        Spacer(
            modifier = Modifier.height(20.dp)
        )

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

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        OutlinedTextField(
            value = tipoMantenimiento,
            onValueChange = {
                tipoMantenimiento = it
            },
            label = {
                Text("Tipo de mantenimiento")
            },
            placeholder = {
                Text("Ej: Cambio de aceite")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        OutlinedTextField(
            value = costo,
            onValueChange = {
                costo = it
            },
            label = {
                Text("Costo")
            },
            placeholder = {
                Text("Ej: 35")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        OutlinedTextField(
            value = taller,
            onValueChange = {
                taller = it
            },
            label = {
                Text("Taller")
            },
            placeholder = {
                Text("Ej: Taller Automotriz Manta")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        OutlinedTextField(
            value = intervaloKilometros,
            onValueChange = {
                intervaloKilometros = it
            },
            label = {
                Text("Recordar mantenimiento en")
            },
            placeholder = {
                Text("Ej: 5000 km")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(15.dp)
        )

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

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Button(
            onClick = {
                // Más adelante guardaremos con Room
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