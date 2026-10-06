package com.example.autocare.screens

// Elementos necesarios para crear la interfaz con Jetpack Compose
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// @Composable indica que esta función crea una parte de la interfaz
@Composable
fun SplashScreen() {

    // Column coloca sus elementos uno debajo del otro
    Column(
        modifier = Modifier
            // Hace que la columna ocupe toda la pantalla
            .fillMaxSize()

            // Color de fondo de la pantalla
            .background(Color(0xFF0D47A1)),

        // Centra los elementos verticalmente
        verticalArrangement = Arrangement.Center,

        // Centra los elementos horizontalmente
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Nombre principal de nuestra aplicación
        Text(
            text = "AutoCare",
            color = Color.White,
            fontSize = 60.sp,
            fontWeight = FontWeight.Bold
        )

        // Frase que aparece debajo del nombre
        Text(
            text = "Cuida tu vehículo",
            color = Color.White,
            fontSize = 18.sp
        )
    }
}