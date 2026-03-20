package com.marvel999.pingmap.di

import androidx.lifecycle.ViewModel
import com.marvel999.pingmap.ui.history.HistoryViewModel
import com.marvel999.pingmap.ui.home.HomeViewModel
import com.marvel999.pingmap.ui.onboarding.OnboardingGateViewModel
import com.marvel999.pingmap.ui.settings.SettingsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AppViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(vm: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OnboardingGateViewModel::class)
    abstract fun bindOnboardingGateViewModel(vm: OnboardingGateViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HistoryViewModel::class)
    abstract fun bindHistoryViewModel(vm: HistoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(vm: SettingsViewModel): ViewModel
}
