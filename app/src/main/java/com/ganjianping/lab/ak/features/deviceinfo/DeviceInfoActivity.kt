package com.ganjianping.lab.ak.features.deviceinfo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ganjianping.lab.ak.common.theme.GJPLabTheme
import com.ganjianping.lab.ak.features.deviceinfo.data.DeviceInfoRepository
import org.koin.android.ext.android.inject

class DeviceInfoActivity : ComponentActivity() {
    private val deviceInfoRepository: DeviceInfoRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GJPLabTheme {
                DeviceInfoScreen(
                    repository = deviceInfoRepository,
                    onBack = ::finish
                )
            }
        }
    }
}
