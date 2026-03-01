package com.marvel999.pingmap.di

import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object ViewModelModule {

    @Provides
    @Singleton
    fun provideViewModelFactory(
        creators: Map<Class<out ViewModel>, @JvmSuppressWildcards javax.inject.Provider<ViewModel>>
    ): ViewModelFactory = ViewModelFactory(creators)
}
