package com.example.autocare.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onIrVehiculo: () -> Unit,
    onIrKilometraje: () -> Unit,
    onIrMantenimiento: () -> Unit,
    onIrHistorial: () -> Unit,
    onIrConfiguracion: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Hola, Danny 👋",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D47A1)
        )

        Text(
            text = "Cuida tu vehículo hoy",
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
                modifier = Modifier.padding(18.dp)
            ) {

                Text(
                    text = "Chevrolet Corsa",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D47A1)
                )

                Text("Evolution 2007")

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "185.420 km",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Inyección",
                    color = Color.Gray
                )
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Button(
            onClick = {
                onIrVehiculo()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0D47A1)
            )
        ) {
            Text("Registrar vehículo")
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Próximos mantenimientos",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF8E1)
            )
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Cambio de aceite",
                    fontWeight = FontWeight.Bold
                )

                Text("Faltan 580 km")
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
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
                    text = "Revisión de frenos",
                    fontWeight = FontWeight.Bold
                )

                Text("Faltan 2.500 km")
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Acciones rápidas",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Button(
                onClick = {
                    onIrKilometraje()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Kilometraje")
            }

            Button(
                onClick = {
                    onIrMantenimiento()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Mantenimiento")
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Button(
                onClick = {
                    onIrHistorial()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Historial")
            }

            Button(
                onClick = {
                    onIrConfiguracion()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Configuración")
            }
        }
    }
}