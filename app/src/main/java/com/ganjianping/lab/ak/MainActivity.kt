package com.ganjianping.lab.ak

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ganjianping.lab.ak.features.httpurlconnection.HttpURLConnectionActivity
import com.ganjianping.lab.ak.features.deviceinfo.DeviceInfoActivity
import com.ganjianping.lab.ak.common.theme.GJPLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GJPLabTheme {
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
