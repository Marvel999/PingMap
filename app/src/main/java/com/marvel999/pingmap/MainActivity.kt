package com.marvel999.pingmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.marvel999.pingmap.core.ui.theme.PingMapTheme
import com.marvel999.pingmap.di.ViewModelFactory
import com.marvel999.pingmap.navigation.PingMapNavGraph

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = (application as PingMapApp).appComponent.viewModelFactory()
        enableEdgeToEdge()
        setContent {
            PingMapTheme {
                PingMapNavGraph(viewModelFactory = viewModelFactory)
            }
        }
    }
}