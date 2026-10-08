
package com.example.autocare.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.autocare.data.Vehiculo
import com.example.autocare.viewmodel.VehiculoViewModel

@Composable
fun PantallaConfiguracion(
    onVolver: () -> Unit,
    onAgregarVehiculo: () -> Unit,
    onEditarVehiculo: (Vehiculo) -> Unit,
    vehiculoViewModel: VehiculoViewModel
) {

    // Preferencias que Maykol conectará posteriormente con DataStore
    var notificacionesActivas by remember {
        mutableStateOf(true)
    }

    var recordatoriosActivos by remember {
        mutableStateOf(true)
    }

    // Lista real de vehículos registrados en Room
    val vehiculos by vehiculoViewModel.vehiculos.collectAsState()

    // Vehículo pendiente de eliminación
    var vehiculoAEliminar by remember {
        mutableStateOf<Vehiculo?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {

        TextButton(
            onClick = onVolver
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

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "Mis vehículos",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (vehiculos.isEmpty()) {

            Text(
                text = "Todavía no tienes vehículos registrados.",
                color = Color.Gray
            )

        } else {

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

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
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D47A1)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(text = "Año: ${vehiculo.anio}")

                            Text(
                                text = "Tipo: ${vehiculo.tipoVehiculo}"
                            )

                            Text(
                                text = "Kilometraje: ${vehiculo.kilometraje} km"
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                OutlinedButton(
                                    onClick = {
                                        onEditarVehiculo(vehiculo)
                                    }
                                ) {
                                    Text("Editar")
                                }

                                OutlinedButton(
                                    onClick = {
                                        vehiculoAEliminar = vehiculo
                                    }
                                ) {
                                    Text(
                                        text = "Eliminar",
                                        color = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onAgregarVehiculo,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0D47A1)
            )
        ) {
            Text(
                text = "+ Agregar vehículo",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "Notificaciones",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

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

        Spacer(modifier = Modifier.height(10.dp))

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

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                // Maykol implementará el guardado con DataStore
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

    // Confirmación antes de eliminar un vehículo
    vehiculoAEliminar?.let { vehiculo ->

        AlertDialog(
            onDismissRequest = {
                vehiculoAEliminar = null
            },
            title = {
                Text("Eliminar vehículo")
            },
            text = {
                Text(
                    "¿Estás seguro de eliminar ${vehiculo.marca} ${vehiculo.modelo}? Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vehiculoViewModel.eliminarVehiculo(vehiculo)
                        vehiculoAEliminar = null
                    }
                ) {
                    Text(
                        text = "Eliminar",
                        color = Color.Red
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        vehiculoAEliminar = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
