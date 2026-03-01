package com.marvel999.pingmap.feature.wifi.di

import android.content.Context
import android.net.wifi.WifiManager
import androidx.lifecycle.ViewModel
import com.marvel999.pingmap.di.ViewModelKey
import com.marvel999.pingmap.feature.wifi.data.WifiRepositoryImpl
import com.marvel999.pingmap.feature.wifi.domain.WifiRepository
import com.marvel999.pingmap.feature.wifi.ui.WifiScanViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
abstract class WifiFeatureModule {

    @Binds
    @IntoMap
    @ViewModelKey(WifiScanViewModel::class)
    abstract fun bindWifiScanViewModel(vm: WifiScanViewModel): ViewModel
}

@Module
object WifiProviderModule {

    @Provides
    @Singleton
    fun provideWifiRepository(context: Context): WifiRepository {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return WifiRepositoryImpl(wifiManager, context.applicationContext)
    }
}
