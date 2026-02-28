package com.marvel999.pingmap.core.network

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Provides a shared [OkHttpClient] for the app (speed test, ping, etc.).
 */
object OkHttpProvider {

    fun create(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
}
