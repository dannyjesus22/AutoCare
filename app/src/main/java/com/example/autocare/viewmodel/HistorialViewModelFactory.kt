
package com.example.autocare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.autocare.data.RegistroKilometrajeRepository


// Esta clase permite crear nuestro HistorialViewModel
// y entregarle el Repository que necesita.
class HistorialViewModelFactory(
    private val repository: RegistroKilometrajeRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        // Comprobamos que se está solicitando
        // el ViewModel correcto.
        if (modelClass.isAssignableFrom(
                HistorialViewModel::class.java
            )
        ) {

            return HistorialViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido"
        )
    }
}
