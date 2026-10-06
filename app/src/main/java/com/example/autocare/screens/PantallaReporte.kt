package com.example.autocare.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaReporte() {

    // Guarda el tipo de reporte seleccionado
    var tipoReporte by remember {
        mutableStateOf("Reporte completo")
    }

    // Opciones que puede seleccionar el usuario
    val opcionesReporte = listOf(
        "Reporte completo",
        "Solo mantenimientos",
        "Solo kilometraje"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Reporte del vehículo",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D47A1)
        )

        Text(
            text = "Consulta y descarga el historial de tu vehículo",
            fontSize = 15.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Vehículo seleccionado
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
                    text = "Kilometraje actual: 185.462 km",
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "Tipo de reporte",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Creamos una opción por cada elemento de la lista
        opcionesReporte.forEach { opcion ->

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = tipoReporte == opcion,
                    onClick = {
                        tipoReporte = opcion
                    }
                )

                Text(
                    text = opcion,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Resumen",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5)
            )
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(text = "Mantenimientos registrados: 4")

                Spacer(modifier = Modifier.height(6.dp))

                Text(text = "Registros de kilometraje: 18")

                Spacer(modifier = Modifier.height(6.dp))

                Text(text = "Kilometraje actual: 185.462 km")
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = {
                // Más adelante mostraremos el reporte real
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0D47A1)
            )
        ) {
            Text("Ver reporte")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                // Más adelante generaremos el archivo PDF
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Descargar PDF")
        }
    }
}