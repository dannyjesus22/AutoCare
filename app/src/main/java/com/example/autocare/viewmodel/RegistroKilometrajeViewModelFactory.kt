
package com.example.autocare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.autocare.data.RegistroKilometrajeRepository

class RegistroKilometrajeViewModelFactory(
    private val repository: RegistroKilometrajeRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(
                RegistroKilometrajeViewModel::class.java
            )
        ) {
            return RegistroKilometrajeViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido"
        )
    }
}
