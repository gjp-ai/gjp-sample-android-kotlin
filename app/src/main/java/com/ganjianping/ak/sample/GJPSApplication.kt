package com.ganjianping.ak.sample

import android.app.Application
import com.ganjianping.ak.sample.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class GJPSApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@GJPSApplication)
            modules(appModule)
        }
    }
}
