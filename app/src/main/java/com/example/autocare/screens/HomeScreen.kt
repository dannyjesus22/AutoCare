
package com.example.autocare.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autocare.data.Vehiculo
import com.example.autocare.data.Mantenimiento

// Colores principales de AutoCare
private val AzulMarino = Color(0xFF101D35)
private val AzulElectrico = Color(0xFF1688ED)
private val FondoClaro = Color(0xFFF4F7FB)
private val TextoSecundario = Color(0xFF68758A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onIrKilometraje: () -> Unit,
    onIrMantenimiento: () -> Unit,
    onIrHistorial: () -> Unit,
    onIrConfiguracion: () -> Unit,
    vehiculos: List<Vehiculo> = emptyList(),
    mantenimientos: List<Mantenimiento> = emptyList(),
    vehiculoSeleccionadoId: Int? = null,
    onSeleccionarVehiculo: (Int) -> Unit = {}
) {
    // Mantener la selección y el filtrado existentes.
    val vehiculoSeleccionado = vehiculos.find {
        it.id == vehiculoSeleccionadoId
    } ?: vehiculos.firstOrNull()

    val proximosMantenimientos = mantenimientos.filter {
        it.vehiculoId == vehiculoSeleccionado?.id
    }

    var menuExpandido by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClaro)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // Encabezado
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Bienvenido a",
                    fontSize = 15.sp,
                    color = TextoSecundario
                )

                Text(
                    text = "AutoCare",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulMarino
                )

                Text(
                    text = "Tu vehículo, siempre al día.",
                    fontSize = 13.sp,
                    color = TextoSecundario
                )
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = AzulMarino
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = "AutoCare",
                    tint = Color.White,
                    modifier = Modifier.padding(14.dp).size(30.dp)
                )
            }
        }

        // Sección de vehículos
        Text(
            text = "Mi vehículo",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AzulMarino
        )

        if (vehiculos.isEmpty()) {

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = AzulElectrico,
                        modifier = Modifier.size(32.dp)
                    )

                    Text(
                        text = "Aún no tienes vehículos registrados",
                        fontWeight = FontWeight.Bold,
                        color = AzulMarino
                    )

                    Text(
                        text = "Puedes registrar tu primer vehículo desde Configuración.",
                        fontSize = 14.sp,
                        color = TextoSecundario
                    )

                    Button(
                        onClick = onIrConfiguracion,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AzulMarino
                        )
                    ) {
                        Text("Ir a Configuración")
                    }
                }
            }

        } else {

            // Selector de vehículos
            ExposedDropdownMenuBox(
                expanded = menuExpandido,
                onExpandedChange = {
                    menuExpandido = !menuExpandido
                }
            ) {
                OutlinedTextField(
                    value = vehiculoSeleccionado?.let {
                        "${it.marca} ${it.modelo}"
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text("Vehículo seleccionado")
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = menuExpandido
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AzulElectrico,
                        focusedLabelColor = AzulElectrico,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(
                            ExposedDropdownMenuAnchorType.PrimaryNotEditable
                        )
                )

                ExposedDropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = {
                        menuExpandido = false
                    }
                ) {
                    vehiculos.forEach { vehiculo ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "${vehiculo.marca} ${vehiculo.modelo} (${vehiculo.anio})"
                                )
                            },
                            onClick = {
                                onSeleccionarVehiculo(vehiculo.id)
                                menuExpandido = false
                            }
                        )
                    }
                }
            }

            // Tarjeta principal del vehículo
            vehiculoSeleccionado?.let { vehiculo ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AzulMarino
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "VEHÍCULO ACTIVO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB5C6DE),
                                letterSpacing = 1.sp
                            )

                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = AzulElectrico,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Text(
                            text = "${vehiculo.marca} ${vehiculo.modelo}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "${vehiculo.anio} • ${vehiculo.tipoVehiculo}",
                            fontSize = 14.sp,
                            color = Color(0xFFB5C6DE)
                        )

                        HorizontalDivider(
                            color = Color(0xFF41516B),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Text(
                            text = "KILOMETRAJE ACTUAL",
                            fontSize = 11.sp,
                            color = Color(0xFFB5C6DE),
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "${vehiculo.kilometraje} km",
                            fontSize = 29.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Próximos mantenimientos
        Text(
            text = "Próximos mantenimientos",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AzulMarino
        )

        if (proximosMantenimientos.isEmpty()) {

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = null,
                        tint = AzulElectrico,
                        modifier = Modifier.size(26.dp)
                    )

                    Column {
                        Text(
                            text = "Sin mantenimientos registrados",
                            fontWeight = FontWeight.Bold,
                            color = AzulMarino
                        )

                        Text(
                            text = "Aquí aparecerán los del vehículo seleccionado.",
                            fontSize = 13.sp,
                            color = TextoSecundario
                        )
                    }
                }
            }

        } else {

            proximosMantenimientos.forEach { mantenimiento ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE8F3FF)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = null,
                                tint = AzulElectrico,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = mantenimiento.titulo,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = AzulMarino
                            )

                            Text(
                                text = "Tipo: ${mantenimiento.tipo}",
                                fontSize = 13.sp,
                                color = TextoSecundario
                            )

                            Text(
                                text = "Próximo: ${mantenimiento.proximoKilometraje} km",
                                fontSize = 13.sp,
                                color = AzulElectrico,
                                fontWeight = FontWeight.SemiBold
                            )

                            if (!mantenimiento.proximaFecha.isNullOrBlank()) {
                                Text(
                                    text = "Fecha: ${mantenimiento.proximaFecha}",
                                    fontSize = 13.sp,
                                    color = TextoSecundario
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Accesos rápidos
        Text(
            text = "Accesos rápidos",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AzulMarino
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AccesoRapidoAutoCare(
                titulo = "Kilometraje",
                icono = Icons.Default.Speed,
                onClick = onIrKilometraje,
                modifier = Modifier.weight(1f)
            )

            AccesoRapidoAutoCare(
                titulo = "Mantenimiento",
                icono = Icons.Default.Build,
                onClick = onIrMantenimiento,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AccesoRapidoAutoCare(
                titulo = "Historial",
                icono = Icons.Default.DateRange,
                onClick = onIrHistorial,
                modifier = Modifier.weight(1f)
            )

            AccesoRapidoAutoCare(
                titulo = "Configuración",
                icono = Icons.Default.Settings,
                onClick = onIrConfiguracion,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

// Tarjeta reutilizable para los accesos rápidos.
@Composable
private fun AccesoRapidoAutoCare(
    titulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(112.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = AzulElectrico,
                modifier = Modifier.size(27.dp)
            )

            Text(
                text = titulo,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = AzulMarino
            )
        }
    }
}
