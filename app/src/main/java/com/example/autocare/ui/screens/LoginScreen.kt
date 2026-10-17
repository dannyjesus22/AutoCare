package com.example.autocare.ui.screens

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
import com.example.autocare.ui.viewmodel.usuarios.EstadoUsuario
import com.example.autocare.ui.viewmodel.usuarios.UsuarioViewModel

private val AzulNoche = Color(0xFF080F20)
private val AzulMarinoLogin = Color(0xFF101D35)
private val AzulElectricoLogin = Color(0xFF1688ED)
private val TextoSuaveLogin = Color(0xFFB5C6DE)

@Composable
fun LoginScreen(
    onIniciarSesion: (Int) -> Unit,
    onIrRegistro: () -> Unit,
    usuarioViewModel: UsuarioViewModel
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    val estado by usuarioViewModel.estado.collectAsState()
    val cargando = estado is EstadoUsuario.Cargando

    // Conservar el inicio de sesión original.
    LaunchedEffect(estado) {
        val resultado = estado

        if (resultado is EstadoUsuario.Exito) {
            onIniciarSesion(resultado.usuario.id)
            usuarioViewModel.limpiarEstado()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AzulNoche)
    ) {

        // Imagen deportiva en la parte superior.
        Image(
            painter = painterResource(id = R.drawable.fondo_login),
            contentDescription = "Vehículo deportivo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .align(Alignment.TopCenter)
        )

        // Degradado para que los textos sean legibles.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            AzulNoche.copy(alpha = 0.30f),
                            AzulNoche.copy(alpha = 0.45f),
                            AzulNoche.copy(alpha = 0.92f),
                            AzulNoche
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

            Spacer(modifier = Modifier.height(60.dp))

            // Marca AutoCare.
            Text(
                text = "AUTOCARE",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp,
                color = Color.White
            )

            Text(
                text = "TU VEHÍCULO. TU CONTROL.",
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold,
                color = AzulElectricoLogin
            )

            // Espacio para apreciar el vehículo.
            Spacer(modifier = Modifier.height(190.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "BIENVENIDO",
                    fontSize = 29.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Inicia sesión para continuar",
                    fontSize = 14.sp,
                    color = TextoSuaveLogin
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo electrónico") },
                    placeholder = { Text("ejemplo@correo.com") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    singleLine = true,
                    enabled = !cargando,
                    shape = RoundedCornerShape(14.dp),
                    colors = coloresCamposLogin(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

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
                    colors = coloresCamposLogin(),
                    modifier = Modifier.fillMaxWidth()
                )

                if (estado is EstadoUsuario.Error) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = (estado as EstadoUsuario.Error).mensaje,
                        color = Color(0xFFFF8B8B),
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        usuarioViewModel.iniciarSesion(
                            correo = correo,
                            contrasena = contrasena
                        )
                    },
                    enabled = !cargando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulElectricoLogin,
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
                            text = "INICIAR SESIÓN",
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
                        onIrRegistro()
                    },
                    enabled = !cargando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "¿No tienes cuenta? ",
                        color = TextoSuaveLogin
                    )

                    Text(
                        text = "Regístrate",
                        color = AzulElectricoLogin,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun coloresCamposLogin() =
    OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = AzulElectricoLogin,
        unfocusedBorderColor = Color(0xFF52627C),
        focusedLabelColor = AzulElectricoLogin,
        unfocusedLabelColor = TextoSuaveLogin,
        focusedPlaceholderColor = Color(0xFF8290A6),
        unfocusedPlaceholderColor = Color(0xFF8290A6),
        cursorColor = AzulElectricoLogin,
        focusedContainerColor = AzulMarinoLogin.copy(alpha = 0.80f),
        unfocusedContainerColor = AzulMarinoLogin.copy(alpha = 0.80f)
    )
