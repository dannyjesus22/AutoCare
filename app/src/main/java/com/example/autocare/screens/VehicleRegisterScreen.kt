package com.example.autocare.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Composable
fun VehicleRegisterScreen(
    onVolver: () -> Unit
) {

    var marca by remember {
        mutableStateOf("")
    }

    var modelo by remember {
        mutableStateOf("")
    }

    var anio by remember {
        mutableStateOf("")
    }

    var kilometraje by remember {
        mutableStateOf("")
    }

    var tipoVehiculo by remember {
        mutableStateOf("Inyección")
    }

    val tipos = listOf(
        "Carburador",
        "Inyección",
        "Eléctrico"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        TextButton(
            onClick = {
                onVolver()
            }
        ) {
            Text("← Volver")
        }

        Text(
            text = "Registrar vehículo",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D47A1)
        )

        Text(
            text = "Ingresa los datos de tu vehículo",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        OutlinedTextField(
            value = marca,
            onValueChange = {
                marca = it
            },
            label = {
                Text("Marca")
            },
            placeholder = {
                Text("Ej: Chevrolet")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        OutlinedTextField(
            value = modelo,
            onValueChange = {
                modelo = it
            },
            label = {
                Text("Modelo")
            },
            placeholder = {
                Text("Ej: Corsa Evolution")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        OutlinedTextField(
            value = anio,
            onValueChange = {
                anio = it
            },
            label = {
                Text("Año")
            },
            placeholder = {
                Text("Ej: 2007")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        OutlinedTextField(
            value = kilometraje,
            onValueChange = {
                kilometraje = it
            },
            label = {
                Text("Kilometraje actual")
            },
            placeholder = {
                Text("Ej: 185462")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

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
                    onClick = {
                        tipoVehiculo = tipo
                    }
                )

                Text(
                    text = tipo
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Button(
            onClick = {
                // Más adelante guardaremos el vehículo con Room
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0D47A1)
            )
        ) {

            Text(
                text = "Guardar vehículo",
                fontSize = 17.sp
            )
        }
    }
}