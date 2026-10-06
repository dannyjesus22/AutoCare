package com.example.autocare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.autocare.navigation.NavegacionApp
import com.example.autocare.ui.theme.AutoCareTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AutoCareTheme {

                // Inicia la navegación de AutoCare
                NavegacionApp()
            }
        }
    }
}