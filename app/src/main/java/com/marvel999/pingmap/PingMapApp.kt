package com.marvel999.pingmap

import android.app.Application
import com.marvel999.pingmap.di.AppComponent
import com.marvel999.pingmap.di.DaggerAppComponent

class PingMapApp : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }
}
