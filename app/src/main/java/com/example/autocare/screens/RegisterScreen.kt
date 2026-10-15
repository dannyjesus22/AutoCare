
package com.example.autocare.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autocare.R
import com.example.autocare.viewmodel.usuarios.EstadoUsuario
import com.example.autocare.viewmodel.usuarios.UsuarioViewModel

private val FondoRegistro = Color(0xFF080F20)
private val AzulMarinoRegistro = Color(0xFF101D35)
private val AzulElectricoRegistro = Color(0xFF1688ED)
private val TextoSuaveRegistro = Color(0xFFB5C6DE)

@Composable
fun RegisterScreen(
    onIrLogin: () -> Unit,
    usuarioViewModel: UsuarioViewModel
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }

    val estado by usuarioViewModel.estado.collectAsState()
    val cargando = estado is EstadoUsuario.Cargando

    // Regresar al Login cuando el registro sea exitoso.
    LaunchedEffect(estado) {
        if (estado is EstadoUsuario.Exito) {
            usuarioViewModel.limpiarEstado()
            onIrLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoRegistro)
    ) {

        // Mismo vehículo deportivo del Login.
        Image(
            painter = painterResource(id = R.drawable.fondo_login),
            contentDescription = "Vehículo deportivo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.48f)
                .align(Alignment.TopCenter)
        )

        // Degradado oscuro para leer correctamente los campos.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            FondoRegistro.copy(alpha = 0.35f),
                            FondoRegistro.copy(alpha = 0.65f),
                            FondoRegistro.copy(alpha = 0.95f),
                            FondoRegistro
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(45.dp))

            Text(
                text = "AUTOCARE",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp,
                color = Color.White
            )

            Text(
                text = "TU VEHÍCULO. TU CONTROL.",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = AzulElectricoRegistro
            )

            // Espacio para mostrar el vehículo.
            Spacer(modifier = Modifier.height(110.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "ÚNETE A AUTOCARE",
                    fontSize = 27.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Crea tu cuenta y toma el control de tu vehículo",
                    fontSize = 14.sp,
                    color = TextoSuaveRegistro
                )

                Spacer(modifier = Modifier.height(22.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") },
                    singleLine = true,
                    enabled = !cargando,
                    shape = RoundedCornerShape(14.dp),
                    colors = coloresCamposRegistro(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(13.dp))

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo electrónico") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    singleLine = true,
                    enabled = !cargando,
                    shape = RoundedCornerShape(14.dp),
                    colors = coloresCamposRegistro(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(13.dp))

                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    singleLine = true,
                    enabled = !cargando,
                    shape = RoundedCornerShape(14.dp),
                    colors = coloresCamposRegistro(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(13.dp))

                OutlinedTextField(
                    value = confirmarContrasena,
                    onValueChange = { confirmarContrasena = it },
                    label = { Text("Confirmar contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    singleLine = true,
                    enabled = !cargando,
                    shape = RoundedCornerShape(14.dp),
                    colors = coloresCamposRegistro(),
                    modifier = Modifier.fillMaxWidth()
                )

                // Conservar los mensajes de error del ViewModel.
                if (estado is EstadoUsuario.Error) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = (estado as EstadoUsuario.Error).mensaje,
                        color = Color(0xFFFF8B8B),
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(23.dp))

                Button(
                    onClick = {
                        usuarioViewModel.registrar(
                            nombre = nombre,
                            correo = correo,
                            contrasena = contrasena,
                            confirmarContrasena = confirmarContrasena
                        )
                    },
                    enabled = !cargando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulElectricoRegistro,
                        contentColor = Color.White
                    )
                ) {
                    if (cargando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "CREAR CUENTA",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = {
                        usuarioViewModel.limpiarEstado()
                        onIrLogin()
                    },
                    enabled = !cargando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "¿Ya tienes cuenta? ",
                        color = TextoSuaveRegistro
                    )

                    Text(
                        text = "Inicia sesión",
                        color = AzulElectricoRegistro,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}

@Composable
private fun coloresCamposRegistro() =
    OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = AzulElectricoRegistro,
        unfocusedBorderColor = Color(0xFF52627C),
        focusedLabelColor = AzulElectricoRegistro,
        unfocusedLabelColor = TextoSuaveRegistro,
        focusedPlaceholderColor = Color(0xFF8290A6),
        unfocusedPlaceholderColor = Color(0xFF8290A6),
        cursorColor = AzulElectricoRegistro,
        focusedContainerColor = AzulMarinoRegistro.copy(alpha = 0.80f),
        unfocusedContainerColor = AzulMarinoRegistro.copy(alpha = 0.80f)
    )
