package com.marvel999.pingmap.ui.home

import com.marvel999.pingmap.feature.wifi.domain.WifiNetwork
import com.marvel999.pingmap.feature.wifi.domain.WiFiScore

data class ScoredNetwork(
    val network: WifiNetwork,
    val score: WiFiScore
)
