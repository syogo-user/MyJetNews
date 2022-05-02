package com.example.myjetnews

import android.app.Application
import com.example.myjetnews.data.AppContainer
import com.example.myjetnews.data.AppContainerImpl

class JetnewsApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}