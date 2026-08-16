package com.ganjianping.lab.ak.features.deviceinfo.data

import android.app.ActivityManager
import android.content.Context
import android.os.Build

class DeviceInfoRepository(private val context: Context) {
    fun read(): List<Pair<String, String>> {
        val memoryInfo = ActivityManager.MemoryInfo()
        (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(memoryInfo)
        val displayMetrics = context.resources.displayMetrics

        return listOf(
            "Android version" to Build.VERSION.RELEASE,
            "SDK level" to Build.VERSION.SDK_INT.toString(),
            "Codename" to Build.VERSION.CODENAME,
            "Screen" to "${displayMetrics.widthPixels} × ${displayMetrics.heightPixels}",
            "Manufacturer" to Build.MANUFACTURER,
            "Model" to Build.MODEL,
            "Device" to Build.DEVICE,
            "Hardware" to Build.HARDWARE,
            "CPU cores" to Runtime.getRuntime().availableProcessors().toString(),
            "Memory" to "${memoryInfo.totalMem / (1024 * 1024)} MB",
            "Supported ABIs" to Build.SUPPORTED_ABIS.joinToString()
        )
    }
}
