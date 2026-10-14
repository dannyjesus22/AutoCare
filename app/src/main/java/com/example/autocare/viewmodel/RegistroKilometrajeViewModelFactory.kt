
package com.example.autocare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.autocare.data.IRegistroKilometrajeRepository

// Factory para crear el ViewModel de kilometraje.
class RegistroKilometrajeViewModelFactory(
    private val repository: IRegistroKilometrajeRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        // Verificamos qué ViewModel nos están solicitando.
        if (modelClass.isAssignableFrom(
                RegistroKilometrajeViewModel::class.java
            )
        ) {
            // Creamos el ViewModel utilizando nuestra interfaz.
            return RegistroKilometrajeViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido"
        )
    }
}
