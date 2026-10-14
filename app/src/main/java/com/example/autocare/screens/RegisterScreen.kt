
package com.example.autocare.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autocare.viewmodel.usuarios.EstadoUsuario
import com.example.autocare.viewmodel.usuarios.UsuarioViewModel

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

    // Cuando el registro termina correctamente,
    // regresar al inicio de sesión.
    LaunchedEffect(estado) {
        if (estado is EstadoUsuario.Exito) {
            usuarioViewModel.limpiarEstado()
            onIrLogin()
        }
    }

    val cargando = estado is EstadoUsuario.Cargando

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "AutoCare",
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D47A1)
        )

        Text(
            text = "Crea tu cuenta",
            fontSize = 17.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(35.dp))

        Text(
            text = "Registrarse",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        Text(
            text = "Ingresa tus datos para crear una cuenta",
            fontSize = 15.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(25.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre completo") },
            singleLine = true,
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            singleLine = true,
            enabled = !cargando,
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
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

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
            modifier = Modifier.fillMaxWidth()
        )

        // Mostrar el error que devuelve el ViewModel.
        if (estado is EstadoUsuario.Error) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = (estado as EstadoUsuario.Error).mensaje,
                color = Color.Red,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

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
                .height(55.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0D47A1)
            )
        ) {
            if (cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.height(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Crear cuenta",
                    fontSize = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(
            onClick = {
                usuarioViewModel.limpiarEstado()
                onIrLogin()
            },
            enabled = !cargando
        ) {
            Text("¿Ya tienes una cuenta? Inicia sesión")
        }
    }
}
