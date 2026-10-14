
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

fun LoginScreen(
    onIniciarSesion: (Int) -> Unit,
    onIrRegistro: () -> Unit,
    usuarioViewModel: UsuarioViewModel
)
 {

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    // Observar el resultado del inicio de sesión.
    val estado by usuarioViewModel.estado.collectAsState()

    val cargando = estado is EstadoUsuario.Cargando

    // Navegar solamente cuando las credenciales sean correctas.


     LaunchedEffect(estado) {
         val resultado = estado

         if (resultado is EstadoUsuario.Exito) {

             // Enviar el ID del usuario que inició sesión.
             onIniciarSesion(resultado.usuario.id)

             // Limpiar el estado después de procesar el resultado.
             usuarioViewModel.limpiarEstado()
         }
     }



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
            text = "Cuida tu vehículo",
            fontSize = 17.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(45.dp))

        Text(
            text = "Iniciar sesión",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        Text(
            text = "Ingresa tus datos para continuar",
            fontSize = 15.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(25.dp))

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
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

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

        // Mostrar mensajes de error.
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
                usuarioViewModel.iniciarSesion(
                    correo = correo,
                    contrasena = contrasena
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
                    text = "Iniciar sesión",
                    fontSize = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        TextButton(
            onClick = {
                usuarioViewModel.limpiarEstado()
                onIrRegistro()
            },
            enabled = !cargando
        ) {
            Text(
                text = "¿No tienes una cuenta? Regístrate",
                color = Color(0xFF0D47A1)
            )
        }
    }
}
