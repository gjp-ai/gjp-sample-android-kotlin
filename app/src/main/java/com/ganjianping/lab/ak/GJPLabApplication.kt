package com.ganjianping.lab.ak

import android.app.Application
import com.ganjianping.lab.ak.di.appModule
import com.ganjianping.lab.ak.integration.firebase.FirebaseIntegration
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin

class GJPLabApplication : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@GJPLabApplication)
            modules(appModule)
        }

        get<FirebaseIntegration>().initialize()
    }

}
