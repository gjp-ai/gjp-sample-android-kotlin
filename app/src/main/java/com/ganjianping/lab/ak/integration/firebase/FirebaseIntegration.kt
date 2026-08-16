package com.ganjianping.lab.ak.integration.firebase

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.ganjianping.lab.ak.BuildConfig

class FirebaseIntegration(
    private val analytics: FirebaseAnalytics,
    private val crashlytics: FirebaseCrashlytics,
    private val remoteConfig: FirebaseRemoteConfig,
    private val performance: FirebasePerformance
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

    fun logFirebaseFeatureOpened() {
        analytics.logEvent(FirebaseConstants.EventFirebaseFeatureOpened, Bundle())
    }

    fun recordCrashlyticsDemo() {
        crashlytics.log("Firebase feature Crashlytics demo")
        crashlytics.setCustomKey(FirebaseConstants.CrashlyticsDemoKey, true)
        crashlytics.recordException(IllegalStateException("GJPLab Crashlytics demo exception"))
    }

    fun runPerformanceDemo(onComplete: (durationMillis: Long) -> Unit) {
        val trace = performance.newTrace(FirebaseConstants.PerformanceDemoTrace)
        val startedAt = SystemClock.elapsedRealtime()
        trace.start()
        Handler(Looper.getMainLooper()).postDelayed({
            trace.stop()
            onComplete(SystemClock.elapsedRealtime() - startedAt)
        }, PERFORMANCE_DEMO_DURATION_MILLIS)
    }

    private companion object {
        const val PERFORMANCE_DEMO_DURATION_MILLIS = 300L
    }
}
