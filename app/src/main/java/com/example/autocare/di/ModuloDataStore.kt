package com.example.autocare.di

import android.content.Context
import com.example.autocare.data.ConfiguracionDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModuloDataStore {

    @Provides
    @Singleton
    fun proporcionarConfiguracionDataStore(
        @ApplicationContext context: Context
    ): ConfiguracionDataStore {
        return ConfiguracionDataStore(context)
    }
}