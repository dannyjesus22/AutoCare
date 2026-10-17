
package com.example.autocare.navigation

// Aquí guardamos los nombres de las rutas de AutoCare.
object Rutas {

    const val SPLASH = "splash"

    const val LOGIN = "login"

    const val REGISTRO = "registro"

    const val INICIO = "inicio"

    const val VEHICULO = "vehiculo"


    // Ruta que recibe el ID del vehículo que vamos a editar.
    const val EDITAR_VEHICULO = "vehiculo/{vehiculoId}"

    // Construye la ruta con el ID real del vehículo.
    fun editarVehiculo(vehiculoId: Int): String {
        return "vehiculo/$vehiculoId"
    }


    const val KILOMETRAJE = "kilometraje"

    const val MANTENIMIENTO = "mantenimiento"

    const val HISTORIAL = "historial"

    const val CONFIGURACION = "configuracion"
}
