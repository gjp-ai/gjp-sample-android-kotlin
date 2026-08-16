package com.ganjianping.lab.ak.features.firebase

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ganjianping.lab.ak.common.theme.GJPLabTheme
import com.ganjianping.lab.ak.integration.firebase.FirebaseConstants
import com.ganjianping.lab.ak.integration.firebase.FirebaseIntegration
import org.koin.android.ext.android.inject

class FirebaseFeatureActivity : ComponentActivity() {
    private val firebaseIntegration: FirebaseIntegration by inject()
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            messagingStatus = "Notifications are disabled; FCM token is still available"
        }
    }
    private var analyticsStatus by mutableStateOf("No event sent yet")
    private var crashlyticsStatus by mutableStateOf("No non-fatal exception recorded yet")
    private var remoteConfigStatus by mutableStateOf("Not fetched yet")
    private var performanceStatus by mutableStateOf("No custom trace completed yet")
    private var messagingStatus by mutableStateOf("Token not loaded yet")
    private var messagingToken by mutableStateOf<String?>(null)
    private var tokenCopied by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        firebaseIntegration.logFirebaseFeatureOpened()
        requestNotificationPermissionIfNeeded()
        setContent {
            GJPLabTheme {
                FirebaseFeatureScreen(
                    analyticsStatus = analyticsStatus,
                    crashlyticsStatus = crashlyticsStatus,
                    remoteConfigStatus = remoteConfigStatus,
                    performanceStatus = performanceStatus,
                    messagingStatus = messagingStatus,
                    messagingToken = messagingToken,
                    tokenCopied = tokenCopied,
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
                    },
                    onFetchMessagingToken = {
                        messagingStatus = "Loading FCM token..."
                        tokenCopied = false
                        firebaseIntegration.fetchMessagingToken { token, error ->
                            messagingToken = token
                            messagingStatus = token?.let { "Full token loaded" }
                                ?: "Token error: ${error?.message ?: "unknown error"}"
                        }
                    },
                    onCopyToken = {
                        messagingToken?.let { token ->
                            val clipboard = getSystemService(ClipboardManager::class.java)
                            clipboard.setPrimaryClip(ClipData.newPlainText("FCM registration token", token))
                            tokenCopied = true
                        }
                    },
                    onSubscribeToTopic = {
                        messagingStatus = "Subscribing to ${FirebaseConstants.MessagingDemoTopic}..."
                        firebaseIntegration.subscribeToMessagingDemoTopic { success ->
                            messagingStatus = if (success) {
                                "Subscribed to ${FirebaseConstants.MessagingDemoTopic}"
                            } else {
                                "Topic subscription failed"
                            }
                        }
                    }
                )
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

}
