
package com.example.autocare.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autocare.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    usuarioActivoId: Int?,
    sesionCargada: Boolean,
    onIrLogin: () -> Unit,
    onIrInicio: () -> Unit
) {

    // Esperar 3 segundos y comprobar si existe una sesión guardada.
    LaunchedEffect(usuarioActivoId, sesionCargada) {
        if (sesionCargada) {
            delay(3000)

            if (usuarioActivoId != null) {
                onIrInicio()
            } else {
                onIrLogin()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF001B4D)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.autocare_splash),
            contentDescription = "Logo de AutoCare",
            modifier = Modifier.size(200.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "AutoCare",
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Cuida tu vehículo",
            color = Color.White,
            fontSize = 18.sp
        )
    }
}
