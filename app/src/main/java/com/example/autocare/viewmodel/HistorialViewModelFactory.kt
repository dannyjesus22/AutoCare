
package com.example.autocare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.autocare.data.IRegistroKilometrajeRepository

// Factory encargada de crear nuestro HistorialViewModel.
class HistorialViewModelFactory(
    private val repository: IRegistroKilometrajeRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        // Comprobamos que nos soliciten HistorialViewModel.
        if (modelClass.isAssignableFrom(HistorialViewModel::class.java)) {

            // Entregamos al ViewModel el Repository recibido.
            return HistorialViewModel(repository) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}
