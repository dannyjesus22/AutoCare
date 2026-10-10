package com.example.autocare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.autocare.data.ConfiguracionDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ConfiguracionViewModel(
    private val dataStore: ConfiguracionDataStore
) : ViewModel() {

    val notificacionesActivadas: StateFlow<Boolean> = dataStore.notificacionesActivadas.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val recordatoriosActivados: StateFlow<Boolean> = dataStore.recordatoriosActivados.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    fun cambiarNotificaciones(activadas: Boolean) {
        viewModelScope.launch {
            dataStore.guardarNotificacionesActivadas(activadas)
        }
    }

    fun cambiarRecordatorios(activados: Boolean) {
        viewModelScope.launch {
            dataStore.guardarRecordatoriosActivados(activados)
        }
    }
}

class ConfiguracionViewModelFactory(
    private val dataStore: ConfiguracionDataStore
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConfiguracionViewModel::class.java)) {
            return ConfiguracionViewModel(dataStore) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}