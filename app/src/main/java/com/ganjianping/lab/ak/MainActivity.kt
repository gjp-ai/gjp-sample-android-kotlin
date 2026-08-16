package com.ganjianping.lab.ak

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ganjianping.lab.ak.features.httpurlconnection.HttpURLConnectionActivity
import com.ganjianping.lab.ak.features.deviceinfo.DeviceInfoActivity
import com.ganjianping.lab.ak.common.theme.GJPLabTheme
import com.ganjianping.lab.ak.integration.firebase.FirebaseIntegration
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val firebaseIntegration: FirebaseIntegration by inject()
    private var remoteConfigLoaded by mutableStateOf(false)
    private var maintenanceEnabled by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadRemoteConfig()
        setContent {
            GJPLabTheme {
                when {
                    !remoteConfigLoaded -> SplashScreen()
                    maintenanceEnabled -> MaintenanceScreen(onRetry = ::loadRemoteConfig)
                    else -> MainScreen(
                        onFeatureSelected = { action ->
                            val activity = when (action) {
                                FeatureAction.DeviceInfo -> DeviceInfoActivity::class.java
                                FeatureAction.HttpURLConnection -> HttpURLConnectionActivity::class.java
                            }
                            startActivity(Intent(this, activity))
                        }
                    )
                }
            }
        }
    }

    private fun loadRemoteConfig() {
        remoteConfigLoaded = false
        firebaseIntegration.fetchMaintenanceMode { enabled ->
            maintenanceEnabled = enabled
            remoteConfigLoaded = true
        }
    }
}
