package com.marvel999.pingmap.di

import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule {

    @Provides
    @Singleton
    fun provideApplication(application: Application): Application = application
}
