package com.sanislo.bvtech

import android.app.Application
import com.sanislo.bvtech.di.getModules
import org.koin.android.java.KoinAndroidApplication
import org.koin.core.KoinApplication
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class BVTechApp : Application() {

    override fun onCreate() {
        super.onCreate()
        KoinApplication.init()
        val koin: KoinApplication = KoinAndroidApplication.create(this)
            .modules(getModules())
        startKoin(GlobalContext(), koin)
    }
}