package com.marvel999.pingmap.feature.devices.di

import android.content.Context
import androidx.lifecycle.ViewModel
import com.marvel999.pingmap.di.ViewModelKey
import com.marvel999.pingmap.feature.devices.data.DeviceRepositoryImpl
import com.marvel999.pingmap.feature.devices.domain.DeviceRepository
import com.marvel999.pingmap.feature.devices.ui.DeviceDiscoveryViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
abstract class DeviceDiscoveryFeatureModule {

    @Binds
    @IntoMap
    @ViewModelKey(DeviceDiscoveryViewModel::class)
    abstract fun bindDeviceDiscoveryViewModel(vm: DeviceDiscoveryViewModel): ViewModel
}

@Module
object DeviceDiscoveryProviderModule {

    @Provides
    @Singleton
    fun provideDeviceRepository(
        context: Context,
        deviceDao: com.marvel999.pingmap.data.local.dao.DeviceDao
    ): DeviceRepository = DeviceRepositoryImpl(context, deviceDao)
}
