package com.ganjianping.ak.sample

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ganjianping.ak.sample.features.httpurlconnection.HttpURLConnectionActivity
import com.ganjianping.ak.sample.features.deviceinfo.DeviceInfoActivity
import com.ganjianping.ak.sample.common.theme.GJPSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GJPSTheme {
                MainScreen(
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
