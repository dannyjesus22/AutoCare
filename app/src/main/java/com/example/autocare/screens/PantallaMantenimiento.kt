package com.example.autocare.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autocare.data.Vehiculo
import com.example.autocare.utils.NotificacionHelper
import com.example.autocare.viewmodel.MantenimientoViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMantenimiento(
    vehiculos: List<Vehiculo> = emptyList(),
    viewModel: MantenimientoViewModel? = null,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val notificacionHelper = remember { NotificacionHelper(context) }

    // Estado del vehículo seleccionado
    var vehiculoSeleccionadoId by remember { mutableStateOf<Int?>(null) }

    // Seleccionar automáticamente si solo existe un vehículo
    LaunchedEffect(vehiculos) {
        if (vehiculos.size == 1 && vehiculoSeleccionadoId == null) {
            vehiculoSeleccionadoId = vehiculos.first().id
        }
    }

    val vehiculoSeleccionado = vehiculos.find { it.id == vehiculoSeleccionadoId }
    val kmActualNum = vehiculoSeleccionado?.kilometraje ?: 0

    // Calendario y formato de fecha
    val calendarioRealizado = remember { Calendar.getInstance() }
    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fechaHoy = formatoFecha.format(Date())

    // Campos del formulario
    var tipoMantenimiento by remember { mutableStateOf("") }
    var fechaMantenimiento by remember { mutableStateOf(fechaHoy) }
    var costoText by remember { mutableStateOf("") }
    var tallerText by remember { mutableStateOf("") }
    var observacionesText by remember { mutableStateOf("") }

    // Configuración de recordatorios
    var recordatorioActivo by remember { mutableStateOf(true) }
    var tipoRecordatorio by remember { mutableStateOf("km") } // "km" o "fecha"
    var cadaKmText by remember { mutableStateOf("") }
    var notificarFaltenKmText by remember { mutableStateOf("") }

    // Estado menú desplegable de vehículos
    var menuExpandido by remember { mutableStateOf(false) }

    // Diálogo DatePicker para la Fecha del Mantenimiento
    val datePickerMantenimiento = DatePickerDialog(
        context,
        { _, anio, mes, dia ->
            calendarioRealizado.set(anio, mes, dia)
            fechaMantenimiento = formatoFecha.format(calendarioRealizado.time)
        },
        calendarioRealizado.get(Calendar.YEAR),
        calendarioRealizado.get(Calendar.MONTH),
        calendarioRealizado.get(Calendar.DAY_OF_MONTH)
    )

    // Cálculos dinámicos por kilometraje
    val cadaKmNum = cadaKmText.toIntOrNull() ?: 0
    val proximoKm = kmActualNum + cadaKmNum

    val azulPrincipal = Color(0xFF0066FF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF004080))
            .verticalScroll(rememberScrollState())
    ) {
        // ENCABEZADO SUPERIOR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onVolver) {
                Text("←", fontSize = 24.sp, color = Color.White)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Registrar mantenimiento",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Agrega la información del mantenimiento",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // CONTENEDOR BLANCO
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // SELECCIONAR VEHÍCULO DE CONFIGURACIÓN
                Text(
                    text = "Selecciona el vehículo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF002B66)
                )

                ExposedDropdownMenuBox(
                    expanded = menuExpandido,
                    onExpandedChange = {
                        if (vehiculos.isNotEmpty()) menuExpandido = !menuExpandido
                    }
                ) {
                    OutlinedTextField(
                        value = vehiculoSeleccionado?.let { "${it.marca} ${it.modelo} ${it.anio}" } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Vehículo") },
                        placeholder = { Text("Selecciona un vehículo") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpandido)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )

                    ExposedDropdownMenu(
                        expanded = menuExpandido,
                        onDismissRequest = { menuExpandido = false }
                    ) {
                        vehiculos.forEach { vehiculo ->
                            DropdownMenuItem(
                                text = { Text("${vehiculo.marca} ${vehiculo.modelo} ${vehiculo.anio}") },
                                onClick = {
                                    vehiculoSeleccionadoId = vehiculo.id
                                    menuExpandido = false
                                }
                            )
                        }
                    }
                }

                if (vehiculos.isEmpty()) {
                    Text(
                        text = "Primero debes registrar un vehículo en Configuración.",
                        color = Color.Red,
                        fontSize = 13.sp
                    )
                }

                // TARJETA INFORMACIÓN DEL VEHÍCULO SELECCIONADO
                vehiculoSeleccionado?.let { vehiculo ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDF4FC))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${vehiculo.marca} ${vehiculo.modelo}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF002B66)
                                )
                                Text(
                                    text = "Año: ${vehiculo.anio}",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFD6E8FF), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = vehiculo.tipoVehiculo,
                                        fontSize = 12.sp,
                                        color = azulPrincipal,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Kilometraje actual: ${vehiculo.kilometraje} km",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF002B66)
                                )
                            }
                        }
                    }
                }

                // TIPO DE MANTENIMIENTO
                CampoConSimbolo(
                    simbolo = "🛠️",
                    label = "Tipo de mantenimiento",
                    placeholder = "Ej: Cambio de aceite y filtro",
                    valor = tipoMantenimiento,
                    onValorChange = { tipoMantenimiento = it }
                )

                // FECHA DEL MANTENIMIENTO
                CampoConSimbolo(
                    simbolo = "📅",
                    label = "Fecha del mantenimiento",
                    placeholder = "DD/MM/YYYY",
                    valor = fechaMantenimiento,
                    onValorChange = { fechaMantenimiento = it },
                    simboloFinal = "🗓️",
                    onClickIconoFinal = { datePickerMantenimiento.show() }
                )

                // KILOMETRAJE ACTUAL (FIJO / BLOQUEADO)
                CampoConSimbolo(
                    simbolo = "⏱️",
                    label = "Kilometraje actual (Fijo)",
                    placeholder = "",
                    valor = if (vehiculoSeleccionado != null) "${vehiculoSeleccionado.kilometraje}" else "0",
                    onValorChange = {},
                    sufijo = "km",
                    enabled = false,
                    readOnly = true
                )

                // COSTO (OPCIONAL)
                CampoConSimbolo(
                    simbolo = "💰",
                    label = "Costo (opcional)",
                    placeholder = "Ej: 35.00",
                    valor = costoText,
                    onValorChange = { costoText = it },
                    sufijo = "USD",
                    keyboardType = KeyboardType.Decimal
                )

                // TALLER (OPCIONAL)
                CampoConSimbolo(
                    simbolo = "🏭",
                    label = "Taller (opcional)",
                    placeholder = "Ej: Taller Automotriz XYZ",
                    valor = tallerText,
                    onValorChange = { tallerText = it }
                )

                // OBSERVACIONES (OPCIONAL)
                Column {
                    CampoConSimbolo(
                        simbolo = "📝",
                        label = "Observaciones (opcional)",
                        placeholder = "Ej: Cambio de aceite 10W-40 y filtro",
                        valor = observacionesText,
                        onValorChange = { if (it.length <= 200) observacionesText = it }
                    )
                    Text(
                        text = "${observacionesText.length}/200",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 2.dp, end = 4.dp)
                    )
                }

                // SECCIÓN CONFIGURAR RECORDATORIO
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEDF4FC))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🔔", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Configurar recordatorio",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF002B66)
                                )
                                Text(
                                    text = "Define cuándo te avisaremos del próximo mantenimiento",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = recordatorioActivo,
                                onCheckedChange = { recordatorioActivo = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = azulPrincipal)
                            )
                        }

                        if (recordatorioActivo) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFE2EDF9), RoundedCornerShape(10.dp))
                                    .padding(4.dp)
                            ) {
                                Button(
                                    onClick = { tipoRecordatorio = "km" },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (tipoRecordatorio == "km") azulPrincipal else Color.Transparent,
                                        contentColor = if (tipoRecordatorio == "km") Color.White else Color.Gray
                                    )
                                ) {
                                    Text("Por kilometraje", fontSize = 12.sp)
                                }
                                Button(
                                    onClick = { tipoRecordatorio = "fecha" },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (tipoRecordatorio == "fecha") azulPrincipal else Color.Transparent,
                                        contentColor = if (tipoRecordatorio == "fecha") Color.White else Color.Gray
                                    )
                                ) {
                                    Text("Por fecha", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (tipoRecordatorio == "km") {
                                CampoConSimbolo(
                                    simbolo = "🔄",
                                    label = "Cada (kilómetros)",
                                    placeholder = "Ej: 5000",
                                    valor = cadaKmText,
                                    onValorChange = { cadaKmText = it },
                                    sufijo = "km",
                                    keyboardType = KeyboardType.Number
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD9E8F9))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "🚩 Próximo mantenimiento: ",
                                            fontSize = 13.sp,
                                            color = Color(0xFF002B66)
                                        )
                                        Text(
                                            text = "$proximoKm km",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF002B66)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                CampoConSimbolo(
                                    simbolo = "🔔",
                                    label = "Notificarme cuando falten",
                                    placeholder = "Ej: 500",
                                    valor = notificarFaltenKmText,
                                    onValorChange = { notificarFaltenKmText = it },
                                    sufijo = "km",
                                    keyboardType = KeyboardType.Number
                                )

                            } else {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD9E8F9))
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "📅 Se enviará recordatorio para la fecha:",
                                            fontSize = 12.sp,
                                            color = Color(0xFF002B66)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = fechaMantenimiento,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF002B66)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // BOTÓN GUARDAR
                Button(
                    onClick = {
                        val vehiculo = vehiculoSeleccionado
                        if (vehiculo == null) {
                            Toast.makeText(context, "Selecciona un vehículo primero", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (tipoMantenimiento.isBlank()) {
                            Toast.makeText(context, "Escribe el tipo de mantenimiento", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val costoDouble = costoText.toDoubleOrNull() ?: 0.0
                        val faltenKmNum = notificarFaltenKmText.toIntOrNull() ?: 0

                        val proximaFechaFinal = if (recordatorioActivo && tipoRecordatorio == "fecha") fechaMantenimiento else null

                        val proximoKmFinal = if (recordatorioActivo && tipoRecordatorio == "km") {
                            if (faltenKmNum in 1 until proximoKm) (proximoKm - faltenKmNum) else proximoKm
                        } else null

                        // Guardar en Room para el vehiculoId correspondiente
                        viewModel?.guardarMantenimiento(
                            vehiculoId = vehiculo.id,
                            titulo = tipoMantenimiento,
                            tipo = "General",
                            kilometrajeRealizado = kmActualNum,
                            proximoKilometraje = proximoKmFinal,
                            fecha = fechaMantenimiento,
                            proximaFecha = proximaFechaFinal,
                            costo = costoDouble,
                            notas = if (tallerText.isNotBlank()) "$tallerText - $observacionesText" else observacionesText
                        )

                        if (recordatorioActivo && tipoRecordatorio == "fecha") {
                            notificacionHelper.programarNotificacionPorFecha(
                                fechaStr = fechaMantenimiento,
                                titulo = "Recordatorio de Mantenimiento",
                                mensaje = "Hoy corresponde realizar: $tipoMantenimiento"
                            )
                        }

                        Toast.makeText(context, "Mantenimiento guardado correctamente", Toast.LENGTH_SHORT).show()
                        onVolver()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = azulPrincipal),
                    enabled = vehiculoSeleccionado != null
                ) {
                    Text(
                        text = "💾 Guardar mantenimiento",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CampoConSimbolo(
    simbolo: String,
    label: String,
    placeholder: String = "",
    valor: String,
    onValorChange: (String) -> Unit,
    sufijo: String? = null,
    simboloFinal: String? = null,
    onClickIconoFinal: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true,
    readOnly: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4F8))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = simbolo, fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        OutlinedTextField(
            value = valor,
            onValueChange = onValorChange,
            label = { Text(label, fontSize = 12.sp) },
            placeholder = { Text(placeholder, fontSize = 12.sp, color = Color.LightGray) },
            singleLine = true,
            modifier = Modifier.weight(1f),
            enabled = enabled,
            readOnly = readOnly,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0066FF),
                unfocusedBorderColor = Color(0xFFD0DCE7),
                disabledBorderColor = Color(0xFFE0E0E0),
                disabledTextColor = Color.DarkGray,
                disabledLabelColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color(0xFFF5F5F5)
            ),
            trailingIcon = {
                if (sufijo != null) {
                    Text(
                        text = sufijo,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                } else if (simboloFinal != null) {
                    IconButton(onClick = { onClickIconoFinal?.invoke() }) {
                        Text(text = simboloFinal, fontSize = 16.sp)
                    }
                }
            }
        )
    }
}