package com.ganjianping.lab.ak.integration.firebase

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.ganjianping.lab.ak.BuildConfig

class FirebaseIntegration(
    private val analytics: FirebaseAnalytics,
    private val crashlytics: FirebaseCrashlytics,
    private val remoteConfig: FirebaseRemoteConfig
) {
    fun initialize() {
        analytics.logEvent(FirebaseConstants.EventAppStarted, Bundle())
        crashlytics.setCustomKey(FirebaseConstants.CrashlyticsAppVersionKey, BuildConfig.VERSION_NAME)

        remoteConfig.apply {
            setConfigSettingsAsync(
                FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(if (BuildConfig.DEBUG) 0 else 3600)
                    .build()
            )
            setDefaultsAsync(mapOf(FirebaseConstants.RemoteConfigMaintenanceEnabled to false))
        }
    }

    fun fetchMaintenanceMode(onComplete: (enabled: Boolean) -> Unit) {
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            onComplete(remoteConfig.getBoolean(FirebaseConstants.RemoteConfigMaintenanceEnabled))
        }
    }
}
