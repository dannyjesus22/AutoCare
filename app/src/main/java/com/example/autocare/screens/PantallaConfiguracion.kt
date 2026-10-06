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
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaConfiguracion(
    onVolver: () -> Unit
) {

    var notificacionesActivas by remember {
        mutableStateOf(true)
    }

    var recordatoriosActivos by remember {
        mutableStateOf(true)
    }

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
            text = "Configuración",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D47A1)
        )

        Text(
            text = "Personaliza las opciones de AutoCare",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        Text(
            text = "Mi vehículo",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
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
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D47A1)
                )

                Text(
                    text = "Evolution 2007"
                )

                Text(
                    text = "Tipo: Inyección"
                )

                Text(
                    text = "Kilometraje: 185.462 km"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        Text(
            text = "Notificaciones",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Activar notificaciones",
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = notificacionesActivas,
                onCheckedChange = {
                    notificacionesActivas = it
                }
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Recordatorios de mantenimiento",
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = recordatoriosActivos,
                onCheckedChange = {
                    recordatoriosActivos = it
                }
            )
        }

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        Button(
            onClick = {
                // Más adelante guardaremos estas preferencias con DataStore
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0D47A1)
            )
        ) {

            Text(
                text = "Guardar configuración",
                fontSize = 17.sp
            )
        }
    }
}