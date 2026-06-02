package com.clndr.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier annotation class DefaultDispatcher

@Qualifier annotation class IoDispatcher

@Qualifier annotation class MainDispatcher

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {

    @Provides @Singleton @DefaultDispatcher
    fun provideDefault(): CoroutineDispatcher = Dispatchers.Default

    @Provides @Singleton @IoDispatcher
    fun provideIo(): CoroutineDispatcher = Dispatchers.IO

    @Provides @Singleton @MainDispatcher
    fun provideMain(): CoroutineDispatcher = Dispatchers.Main
}
