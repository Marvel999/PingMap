package com.marvel999.pingmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.marvel999.pingmap.core.ui.theme.PingMapTheme
import com.marvel999.pingmap.di.ViewModelFactory
import com.marvel999.pingmap.navigation.PingMapNavGraph
import com.marvel999.pingmap.ui.onboarding.OnboardingGateViewModel
import com.marvel999.pingmap.ui.onboarding.OnboardingScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = (application as PingMapApp).appComponent.viewModelFactory()
        enableEdgeToEdge()
        setContent {
            PingMapTheme {
                val gateViewModel: OnboardingGateViewModel = viewModel(factory = viewModelFactory)
                val completed by gateViewModel.onboardingCompleted.collectAsState(initial = false)
                if (completed) {
                    PingMapNavGraph(viewModelFactory = viewModelFactory)
                } else {
                    OnboardingScreen(onComplete = { gateViewModel.completeOnboarding() })
                }
            }
        }
    }
}