package com.marvel999.pingmap.di

import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object ViewModelModule {

    @Provides
    @Singleton
    fun provideViewModelFactory(): ViewModelFactory =
        ViewModelFactory(emptyMap())
}
