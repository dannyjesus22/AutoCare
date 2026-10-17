package com.example.autocare.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

@Composable
fun SplashScreen(
    usuarioActivoId: Int?,
    sesionCargada: Boolean,
    onIrLogin: () -> Unit,
    onIrInicio: () -> Unit
) {

    // Cuando DataStore termina de leer la sesión,
    // dirigir al usuario sin esperar 3 segundos.
    LaunchedEffect(usuarioActivoId, sesionCargada) {
        if (sesionCargada) {
            if (usuarioActivoId != null) {
                onIrInicio()
            } else {
                onIrLogin()
            }
        }
    }

    // Pantalla temporal mientras se comprueba la sesión.
    Box(
        modifier = Modifier.fillMaxSize()
    )
}
