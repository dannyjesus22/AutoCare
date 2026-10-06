package com.example.autocare.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.autocare.screens.HomeScreen
import com.example.autocare.screens.LoginScreen
import com.example.autocare.screens.PantallaConfiguracion
import com.example.autocare.screens.PantallaHistorial
import com.example.autocare.screens.PantallaKilometraje
import com.example.autocare.screens.PantallaMantenimiento
import com.example.autocare.screens.RegisterScreen
import com.example.autocare.screens.SplashScreen
import com.example.autocare.screens.VehicleRegisterScreen

@Composable
fun NavegacionApp() {

    // La aplicación comienza mostrando el Splash
    var pantallaActual by remember {
        mutableStateOf("splash")
    }

    when (pantallaActual) {

        "splash" -> {
            SplashScreen(
                onIrLogin = {
                    pantallaActual = "login"
                }
            )
        }

        "login" -> {
            LoginScreen(
                onIniciarSesion = {
                    pantallaActual = "inicio"
                },
                onIrRegistro = {
                    pantallaActual = "registro"
                }
            )
        }

        "registro" -> {
            RegisterScreen(
                onIrLogin = {
                    pantallaActual = "login"
                }
            )
        }

        "inicio" -> {
            HomeScreen(
                onIrVehiculo = {
                    pantallaActual = "vehiculo"
                },
                onIrKilometraje = {
                    pantallaActual = "kilometraje"
                },
                onIrMantenimiento = {
                    pantallaActual = "mantenimiento"
                },
                onIrHistorial = {
                    pantallaActual = "historial"
                },
                onIrConfiguracion = {
                    pantallaActual = "configuracion"
                }
            )
        }

        "vehiculo" -> {
            VehicleRegisterScreen(
                onVolver = {
                    pantallaActual = "inicio"
                }
            )
        }

        "kilometraje" -> {
            PantallaKilometraje(
                onVolver = {
                    pantallaActual = "inicio"
                }
            )
        }

        "mantenimiento" -> {
            PantallaMantenimiento(
                onVolver = {
                    pantallaActual = "inicio"
                }
            )
        }

        "historial" -> {
            PantallaHistorial(
                onVolver = {
                    pantallaActual = "inicio"
                }
            )
        }

        "configuracion" -> {
            PantallaConfiguracion(
                onVolver = {
                    pantallaActual = "inicio"
                }
            )
        }
    }
}