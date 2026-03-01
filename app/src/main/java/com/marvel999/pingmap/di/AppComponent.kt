package com.marvel999.pingmap.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        AppViewModelModule::class,
        ViewModelModule::class,
        DatabaseModule::class,
        PreferencesModule::class,
        com.marvel999.pingmap.feature.wifi.di.WifiFeatureModule::class,
        com.marvel999.pingmap.feature.wifi.di.WifiProviderModule::class,
        com.marvel999.pingmap.feature.speedtest.di.SpeedTestFeatureModule::class,
        com.marvel999.pingmap.feature.speedtest.di.SpeedTestProviderModule::class,
        com.marvel999.pingmap.feature.devices.di.DeviceDiscoveryFeatureModule::class,
        com.marvel999.pingmap.feature.devices.di.DeviceDiscoveryProviderModule::class,
        com.marvel999.pingmap.feature.ping.di.PingFeatureModule::class,
        com.marvel999.pingmap.feature.ping.di.PingProviderModule::class,
        com.marvel999.pingmap.feature.portscanner.di.PortScannerFeatureModule::class,
        com.marvel999.pingmap.feature.portscanner.di.PortScannerProviderModule::class,
        com.marvel999.pingmap.feature.signalmap.di.SignalMapFeatureModule::class,
        com.marvel999.pingmap.feature.signalmap.di.SignalMapProviderModule::class
    ]
)
interface AppComponent {

    fun viewModelFactory(): ViewModelFactory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}
