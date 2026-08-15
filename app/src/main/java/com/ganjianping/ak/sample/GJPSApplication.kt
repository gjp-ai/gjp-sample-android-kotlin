package com.ganjianping.ak.sample

import android.app.Application
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.ganjianping.ak.sample.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class GJPSApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initializeFirebase()

        startKoin {
            androidLogger()
            androidContext(this@GJPSApplication)
            modules(appModule)
        }
    }

    private fun initializeFirebase() {
        FirebaseAnalytics.getInstance(this).logEvent("app_started", Bundle())
        FirebaseCrashlytics.getInstance().setCustomKey("app_version", BuildConfig.VERSION_NAME)

        FirebaseRemoteConfig.getInstance().apply {
            setConfigSettingsAsync(
                com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(if (BuildConfig.DEBUG) 0 else 3600)
                    .build()
            )
            setDefaultsAsync(mapOf("sample_feature_enabled" to false))
            fetchAndActivate()
        }
    }
}
