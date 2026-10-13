
package com.example.autocare.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autocare.data.RegistroKilometraje
import com.example.autocare.data.Vehiculo
import com.example.autocare.viewmodel.HistorialUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHistorial(
    onVolver: () -> Unit,
    vehiculos: List<Vehiculo>,
    registros: List<RegistroKilometraje>,
    vehiculoSeleccionadoId: Int?,
    onSeleccionarVehiculo: (Int) -> Unit,
    estadoHistorial: HistorialUiState,
    onGenerarPdf: () -> Unit = {}
) {

    // Controla la pestaña que estamos visualizando.
    // 0 = Kilometraje
    // 1 = Mantenimiento
    var pestanaSeleccionada by remember {
        mutableIntStateOf(0)
    }

    // Buscamos el vehículo seleccionado.
    // Si no existe una selección, usamos el primero.
    val vehiculoSeleccionado = vehiculos.find {
        it.id == vehiculoSeleccionadoId
    } ?: vehiculos.firstOrNull()

    // Controla si el menú de vehículos está abierto.
    var menuAbierto by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {

        // Botón para regresar al inicio.
        TextButton(
            onClick = onVolver
        ) {
            Text("← Volver")
        }

        // Título principal.
        Text(
            text = "Historial",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D47A1)
        )

        Text(
            text = "Consulta la actividad de tu vehículo",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // Si no existen vehículos, mostramos un mensaje.
        if (vehiculos.isEmpty()) {

            Text(
                text = "No tienes vehículos registrados.",
                color = Color.Gray
            )

        } else {

            // Menú para seleccionar un vehículo.
            ExposedDropdownMenuBox(
                expanded = menuAbierto,
                onExpandedChange = {
                    menuAbierto = !menuAbierto
                }
            ) {

                OutlinedTextField(
                    value = vehiculoSeleccionado?.let {
                        "${it.marca} ${it.modelo}"
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text("Seleccionar vehículo")
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = menuAbierto
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(
                            ExposedDropdownMenuAnchorType.PrimaryNotEditable
                        )
                )

                ExposedDropdownMenu(
                    expanded = menuAbierto,
                    onDismissRequest = {
                        menuAbierto = false
                    }
                ) {

                    vehiculos.forEach { vehiculo ->

                        DropdownMenuItem(
                            text = {
                                Text(
                                    "${vehiculo.marca} ${vehiculo.modelo} ${vehiculo.anio}"
                                )
                            },
                            onClick = {

                                // Avisamos a NavegacionApp
                                // qué vehículo seleccionó el usuario.
                                onSeleccionarVehiculo(vehiculo.id)

                                menuAbierto = false
                            }
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            // Tarjeta con datos reales del vehículo.
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
                        text = vehiculoSeleccionado?.let {
                            "${it.marca} ${it.modelo} ${it.anio}"
                        } ?: "Vehículo no seleccionado",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = "Kilometraje actual: " +
                                "${vehiculoSeleccionado?.kilometraje ?: 0} km"
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // Pestañas del historial.
            TabRow(
                selectedTabIndex = pestanaSeleccionada
            ) {

                Tab(
                    selected = pestanaSeleccionada == 0,
                    onClick = {
                        pestanaSeleccionada = 0
                    },
                    text = {
                        Text("Kilometraje")
                    }
                )

                Tab(
                    selected = pestanaSeleccionada == 1,
                    onClick = {
                        pestanaSeleccionada = 1
                    },
                    text = {
                        Text("Mantenimiento")
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            // Mostramos el contenido según la pestaña.
            when (pestanaSeleccionada) {


                0 -> {

                    // Mostramos el contenido según el estado del ViewModel.
                    when (val estado = estadoHistorial) {

                        is HistorialUiState.Loading -> {

                            // Indicador mientras se consultan los registros.
                            CircularProgressIndicator()
                        }

                        is HistorialUiState.Success -> {

                            // Mostramos los registros del vehículo seleccionado.
                            val registrosDelVehiculo = estado.registros.filter {
                                it.vehiculoId == vehiculoSeleccionado?.id
                            }

                            HistorialKilometraje(
                                registros = registrosDelVehiculo
                            )
                        }

                        is HistorialUiState.Error -> {

                            // Mensaje cuando ocurre un error.
                            Text(
                                text = estado.mensaje,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }


                1 -> {

                    // Pendiente de integrar el trabajo de Maykol.
                    Text(
                        text = "Próximamente mostraremos aquí " +
                                "el historial de mantenimiento.",
                        color = Color.Gray
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // Botón reservado para la generación del PDF.
            Button(
                onClick = onGenerarPdf,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                enabled = false,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0D47A1)
                )
            ) {

                Text(
                    text = "Generar reporte PDF",
                    fontSize = 17.sp
                )
            }
        }
    }
}
