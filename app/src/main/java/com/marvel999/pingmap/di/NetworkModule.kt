package com.marvel999.pingmap.di

import com.marvel999.pingmap.core.network.OkHttpProvider
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpProvider.create()
}
