package com.marvel999.pingmap.feature.signalmap.di

import android.content.Context
import androidx.lifecycle.ViewModel
import com.marvel999.pingmap.di.ViewModelKey
import com.marvel999.pingmap.feature.signalmap.data.SignalMapRepositoryImpl
import com.marvel999.pingmap.feature.signalmap.domain.SignalMapRepository
import com.marvel999.pingmap.feature.signalmap.ui.SignalMapViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
abstract class SignalMapFeatureModule {

    @Binds
    @IntoMap
    @ViewModelKey(SignalMapViewModel::class)
    abstract fun bindSignalMapViewModel(vm: SignalMapViewModel): ViewModel
}

@Module
object SignalMapProviderModule {

    @Provides
    @Singleton
    fun provideSignalMapRepository(
        context: Context,
        signalPointDao: com.marvel999.pingmap.data.local.dao.SignalPointDao
    ): SignalMapRepository = SignalMapRepositoryImpl(context, signalPointDao)
}
