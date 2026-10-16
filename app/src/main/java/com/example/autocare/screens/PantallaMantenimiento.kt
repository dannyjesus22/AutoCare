package com.example.autocare.screens

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
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
    val notificacionHelper = remember(context) { NotificacionHelper(context) }

    val azulPrincipal = Color(0xFF0066FF)
    val azulOscuro = Color(0xFF002B66)
    val fondoTarjeta = Color(0xFFEDF4FC)

    val formatoFecha = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
            isLenient = false
        }
    }

    val fechaHoy = remember { formatoFecha.format(Date()) }

    var vehiculoSeleccionadoId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(vehiculos) {
        if (vehiculos.size == 1 && vehiculoSeleccionadoId == null) {
            vehiculoSeleccionadoId = vehiculos.first().id
        }
        if (vehiculos.none { it.id == vehiculoSeleccionadoId }) {
            vehiculoSeleccionadoId = vehiculos.firstOrNull()?.id
        }
    }

    val vehiculoSeleccionado = vehiculos.find { it.id == vehiculoSeleccionadoId }
    val kmActualNum = vehiculoSeleccionado?.kilometraje ?: 0

    var tipoMantenimiento by remember { mutableStateOf("") }
    var fechaMantenimiento by remember { mutableStateOf(fechaHoy) }
    var costoText by remember { mutableStateOf("") }
    var tallerText by remember { mutableStateOf("") }
    var observacionesText by remember { mutableStateOf("") }

    var recordatorioActivo by remember { mutableStateOf(true) }
    var tipoRecordatorio by remember { mutableStateOf("km") }
    var cadaKmText by remember { mutableStateOf("") }

    var menuExpandido by remember { mutableStateOf(false) }

    val solicitarPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (!concedido) {
            Toast.makeText(
                context,
                "Puedes activar las notificaciones desde Ajustes",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun verificarPermisoNotificaciones() {
        if (
            Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            solicitarPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val calendarioRealizado = remember { Calendar.getInstance() }

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

    val cadaKmNum = cadaKmText.toIntOrNull() ?: 0
    val proximoKm = kmActualNum.toLong() + cadaKmNum.toLong()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF004080))
            .verticalScroll(rememberScrollState())
    ) {
        // ENCABEZADO
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onVolver) {
                Text(text = "←", fontSize = 24.sp, color = Color.White)
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

        // CONTENEDOR PRINCIPAL
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // SELECCIÓN DEL VEHÍCULO
                Text(
                    text = "Selecciona el vehículo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = azulOscuro
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
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpandido) },
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

                // INFORMACIÓN DEL VEHÍCULO
                vehiculoSeleccionado?.let { vehiculo ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = fondoTarjeta)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "${vehiculo.marca} ${vehiculo.modelo}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = azulOscuro
                            )
                            Text(
                                text = "Año: ${vehiculo.anio}",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = vehiculo.tipoVehiculo,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = azulPrincipal
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Kilometraje actual: ${vehiculo.kilometraje} km",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = azulOscuro
                            )
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

                // KILOMETRAJE ACTUAL
                CampoConSimbolo(
                    simbolo = "⏱️",
                    label = "Kilometraje actual (Fijo)",
                    valor = kmActualNum.toString(),
                    onValorChange = {},
                    sufijo = "km",
                    enabled = false,
                    readOnly = true
                )

                // COSTO
                CampoConSimbolo(
                    simbolo = "💰",
                    label = "Costo (opcional)",
                    placeholder = "Ej: 35.00",
                    valor = costoText,
                    onValorChange = { costoText = it },
                    sufijo = "USD",
                    keyboardType = KeyboardType.Decimal
                )

                // TALLER
                CampoConSimbolo(
                    simbolo = "🏭",
                    label = "Taller (opcional)",
                    placeholder = "Ej: Taller Automotriz XYZ",
                    valor = tallerText,
                    onValorChange = { tallerText = it }
                )

                // OBSERVACIONES
                Column {
                    CampoConSimbolo(
                        simbolo = "📝",
                        label = "Observaciones (opcional)",
                        placeholder = "Ej: Cambio de aceite 10W-40 y filtro",
                        valor = observacionesText,
                        onValorChange = {
                            if (it.length <= 200) observacionesText = it
                        }
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

                // RECORDATORIOS
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = fondoTarjeta)
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
                                    color = azulOscuro
                                )
                                Text(
                                    text = "Define cuándo te avisaremos del mantenimiento",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = recordatorioActivo,
                                onCheckedChange = {
                                    recordatorioActivo = it
                                    if (it) verificarPermisoNotificaciones()
                                },
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
                                            text = "🚩 Notificar al alcanzar: ",
                                            fontSize = 13.sp,
                                            color = azulOscuro
                                        )
                                        Text(
                                            text = "$proximoKm km",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = azulOscuro
                                        )
                                    }
                                }

                            } else {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD9E8F9))
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "📅 Se notificará en la fecha registrada arriba:",
                                            fontSize = 12.sp,
                                            color = azulOscuro
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = fechaMantenimiento,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = azulOscuro
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // GUARDAR MANTENIMIENTO
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

                        val fechaRealizada = try {
                            formatoFecha.parse(fechaMantenimiento)
                        } catch (e: Exception) {
                            null
                        }

                        if (fechaRealizada == null) {
                            Toast.makeText(context, "Ingresa una fecha válida", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val costoDouble = costoText.toDoubleOrNull() ?: 0.0
                        if (costoDouble < 0) {
                            Toast.makeText(context, "El costo no puede ser negativo", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        var proximaFechaFinal: String? = null
                        var proximoKmFinal: Int? = null

                        if (recordatorioActivo) {
                            if (tipoRecordatorio == "fecha") {
                                proximaFechaFinal = fechaMantenimiento
                            } else {
                                val cadaKm = cadaKmText.toIntOrNull()
                                if (cadaKm == null || cadaKm <= 0) {
                                    Toast.makeText(context, "Ingresa una cantidad válida de kilómetros", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val kilometrajeAviso = kmActualNum.toLong() + cadaKm.toLong()
                                if (kilometrajeAviso > Int.MAX_VALUE) {
                                    Toast.makeText(context, "El kilometraje supera el límite permitido", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                proximoKmFinal = kilometrajeAviso.toInt()
                            }
                        }

                        if (viewModel == null) {
                            Toast.makeText(context, "No se pudo acceder al registro de mantenimiento", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // Guardar en Room con estado completado = false (PENDIENTE)
                        viewModel.guardarMantenimiento(
                            vehiculoId = vehiculo.id,
                            titulo = tipoMantenimiento.trim(),
                            tipo = "General",
                            kilometrajeRealizado = kmActualNum,
                            proximoKilometraje = proximoKmFinal,
                            fecha = fechaMantenimiento,
                            proximaFecha = proximaFechaFinal,
                            costo = costoDouble,
                            notas = if (tallerText.isNotBlank()) {
                                "${tallerText.trim()} - ${observacionesText.trim()}"
                            } else {
                                observacionesText.trim()
                            },
                            completado = false
                        )

                        if (recordatorioActivo && tipoRecordatorio == "fecha") {
                            verificarPermisoNotificaciones()
                            notificacionHelper.programarNotificacionPorFecha(
                                fechaStr = fechaMantenimiento,
                                titulo = "Recordatorio de Mantenimiento",
                                mensaje = "Hoy corresponde realizar: $tipoMantenimiento"
                            )
                        } else if (recordatorioActivo) {
                            verificarPermisoNotificaciones()
                        }

                        Toast.makeText(context, "Mantenimiento registrado como Pendiente", Toast.LENGTH_SHORT).show()
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