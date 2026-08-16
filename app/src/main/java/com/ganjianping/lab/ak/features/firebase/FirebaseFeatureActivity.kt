package com.ganjianping.lab.ak.features.firebase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ganjianping.lab.ak.common.theme.GJPLabTheme
import com.ganjianping.lab.ak.integration.firebase.FirebaseIntegration
import org.koin.android.ext.android.inject

class FirebaseFeatureActivity : ComponentActivity() {
    private val firebaseIntegration: FirebaseIntegration by inject()
    private var analyticsStatus by mutableStateOf("No event sent yet")
    private var crashlyticsStatus by mutableStateOf("No non-fatal exception recorded yet")
    private var remoteConfigStatus by mutableStateOf("Not fetched yet")
    private var performanceStatus by mutableStateOf("No custom trace completed yet")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        firebaseIntegration.logFirebaseFeatureOpened()
        setContent {
            GJPLabTheme {
                FirebaseFeatureScreen(
                    analyticsStatus = analyticsStatus,
                    crashlyticsStatus = crashlyticsStatus,
                    remoteConfigStatus = remoteConfigStatus,
                    performanceStatus = performanceStatus,
                    onBack = ::finish,
                    onLogAnalytics = {
                        firebaseIntegration.logFirebaseFeatureOpened()
                        analyticsStatus = "firebase_feature_opened sent"
                    },
                    onRecordCrashlytics = {
                        firebaseIntegration.recordCrashlyticsDemo()
                        crashlyticsStatus = "Non-fatal demo exception recorded"
                    },
                    onFetchRemoteConfig = {
                        remoteConfigStatus = "Fetching maintenance flag..."
                        firebaseIntegration.fetchMaintenanceMode { enabled ->
                            remoteConfigStatus = "gjp_lab_maintenance_enabled = $enabled"
                        }
                    },
                    onRunPerformance = {
                        performanceStatus = "Running custom trace..."
                        firebaseIntegration.runPerformanceDemo { durationMillis ->
                            performanceStatus = "firebase_demo_trace completed in ${durationMillis} ms"
                        }
                    }
                )
            }
        }
    }
}
