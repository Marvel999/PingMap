package com.marvel999.pingmap.di

import android.content.Context
import com.marvel999.pingmap.data.preferences.UserPreferencesDataStore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object PreferencesModule {

    @Provides
    @Singleton
    fun provideUserPreferencesDataStore(context: Context): UserPreferencesDataStore =
        UserPreferencesDataStore(context)
}