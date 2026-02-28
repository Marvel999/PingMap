package com.marvel999.pingmap.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ViewModelModule::class])
interface AppComponent {

    fun viewModelFactory(): ViewModelFactory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}
