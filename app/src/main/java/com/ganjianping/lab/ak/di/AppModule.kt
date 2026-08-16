package com.ganjianping.lab.ak.di

import com.ganjianping.lab.ak.features.deviceinfo.data.DeviceInfoRepository
import com.ganjianping.lab.ak.features.httpurlconnection.HttpURLConnectionRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { DeviceInfoRepository(androidContext()) }
    single { HttpURLConnectionRepository() }
}
