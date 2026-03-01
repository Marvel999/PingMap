package com.marvel999.pingmap.feature.portscanner.di

import androidx.lifecycle.ViewModel
import com.marvel999.pingmap.di.ViewModelKey
import com.marvel999.pingmap.feature.portscanner.data.PortScannerRepositoryImpl
import com.marvel999.pingmap.feature.portscanner.domain.PortScannerRepository
import com.marvel999.pingmap.feature.portscanner.ui.PortScanViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
abstract class PortScannerFeatureModule {

    @Binds
    @IntoMap
    @ViewModelKey(PortScanViewModel::class)
    abstract fun bindPortScanViewModel(vm: PortScanViewModel): ViewModel
}

@Module
object PortScannerProviderModule {

    @Provides
    @Singleton
    fun providePortScannerRepository(): PortScannerRepository = PortScannerRepositoryImpl()
}
