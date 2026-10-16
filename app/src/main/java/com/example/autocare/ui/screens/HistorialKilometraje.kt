package com.example.autocare.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autocare.data.entities.RegistroKilometraje
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistorialKilometraje(
    registros: List<RegistroKilometraje>
) {

    // Si no existen registros, mostramos un mensaje.
    if (registros.isEmpty()) {

        Text(
            text = "Todavía no tienes registros de kilometraje.",
            color = Color.Gray
        )

    } else {

        // Recorremos todos los registros recibidos.
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            registros.forEachIndexed { indice, registro ->

                // Convertimos la fecha guardada en milisegundos
                // a un formato que pueda leer el usuario.
                val formatoFecha = SimpleDateFormat(
                    "dd/MM/yyyy HH:mm",
                    Locale.getDefault()
                )

                val fechaFormateada = formatoFecha.format(
                    Date(registro.fecha)
                )

                // Calculamos los kilómetros recorridos entre
                // este registro y el registro anterior.
                val registroAnterior = registros.getOrNull(indice + 1)

                val recorrido = registroAnterior?.let {
                    registro.kilometrajeActual - it.kilometrajeActual
                }

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
                            text = fechaFormateada,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (recorrido != null) {
                            Text(
                                text = "Recorrido: $recorrido km"
                            )
                        }

                        Text(
                            text = "Kilometraje total: ${registro.kilometrajeActual} km"
                        )
                    }
                }
            }
        }
    }
}
