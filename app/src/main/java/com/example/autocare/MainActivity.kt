
package com.example.autocare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.autocare.navigation.NavegacionPrueba
import com.example.autocare.ui.theme.AutoCareTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // Instala el Splash oficial antes de crear la Activity.
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            AutoCareTheme {
                NavegacionPrueba()
            }
        }
    }
}
