package com.marvel999.pingmap.feature.speedtest.di

import androidx.lifecycle.ViewModel
import com.marvel999.pingmap.di.ViewModelKey
import com.marvel999.pingmap.feature.speedtest.data.SpeedTestRepositoryImpl
import com.marvel999.pingmap.feature.speedtest.domain.SpeedTestRepository
import com.marvel999.pingmap.feature.speedtest.ui.SpeedTestViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
abstract class SpeedTestFeatureModule {

    @Binds
    @IntoMap
    @ViewModelKey(SpeedTestViewModel::class)
    abstract fun bindSpeedTestViewModel(vm: SpeedTestViewModel): ViewModel
}

@Module
object SpeedTestProviderModule {

    @Provides
    @Singleton
    fun provideSpeedTestRepository(
        okHttpClient: OkHttpClient,
        speedTestDao: com.marvel999.pingmap.data.local.dao.SpeedTestDao
    ): SpeedTestRepository = SpeedTestRepositoryImpl(okHttpClient, speedTestDao)
}
