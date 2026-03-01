package com.marvel999.pingmap.feature.ping.di

import androidx.lifecycle.ViewModel
import com.marvel999.pingmap.di.ViewModelKey
import com.marvel999.pingmap.feature.ping.data.PingRepositoryImpl
import com.marvel999.pingmap.feature.ping.domain.PingRepository
import com.marvel999.pingmap.feature.ping.ui.PingViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
abstract class PingFeatureModule {

    @Binds
    @IntoMap
    @ViewModelKey(PingViewModel::class)
    abstract fun bindPingViewModel(vm: PingViewModel): ViewModel
}

@Module
object PingProviderModule {

    @Provides
    @Singleton
    fun providePingRepository(): PingRepository = PingRepositoryImpl()
}
