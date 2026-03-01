package com.marvel999.pingmap.feature.portscanner.domain

data class PortResult(
    val port: Int,
    val isOpen: Boolean,
    val serviceName: String
)
