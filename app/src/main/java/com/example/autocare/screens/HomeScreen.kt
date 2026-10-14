
package com.example.autocare.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autocare.data.Vehiculo

@Composable
fun HomeScreen(
    onIrKilometraje: () -> Unit,
    onIrMantenimiento: () -> Unit,
    onIrHistorial: () -> Unit,
    onIrConfiguracion: () -> Unit,

    // Lista de vehículos que viene desde Room
    vehiculos: List<Vehiculo> = emptyList()
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {

        Text(
            text = "Bienvenido a AutoCare 👋",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D47A1)
        )

        Text(
            text = "Cuida tu vehículo hoy",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Mis vehículos (${vehiculos.size})",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Mostrar un mensaje si todavía no hay vehículos
        if (vehiculos.isEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text("Todavía no tienes vehículos registrados.")

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Puedes agregar uno desde Configuración.",
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(onClick = onIrConfiguracion) {
                        Text("Ir a Configuración")
                    }
                }
            }

        } else {

            // Mostrar todos los vehículos registrados en Room
            vehiculos.forEach { vehiculo ->

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
                            text = "${vehiculo.marca} ${vehiculo.modelo}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0D47A1)
                        )

                        Text("Año: ${vehiculo.anio}")

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = "Kilometraje: ${vehiculo.kilometraje} km",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Tipo: ${vehiculo.tipoVehiculo}",
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Próximos mantenimientos",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Pendiente de conectar con los mantenimientos de Maykol
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
                    text = "Sin información de mantenimientos",
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Aquí aparecerán tus próximos mantenimientos registrados.",
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Acciones rápidas",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Button(
                onClick = onIrKilometraje,
                modifier = Modifier.weight(1f)
            ) {
                Text("Kilometraje")
            }

            Button(
                onClick = onIrMantenimiento,
                modifier = Modifier.weight(1f)
            ) {
                Text("Mantenimiento")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Button(
                onClick = onIrHistorial,
                modifier = Modifier.weight(1f)
            ) {
                Text("Historial")
            }

            Button(
                onClick = onIrConfiguracion,
                modifier = Modifier.weight(1f)
            ) {
                Text("Configuración")
            }
        }
    }
}
