package com.ganjianping.ak.sample.di

import com.ganjianping.ak.sample.features.deviceinfo.data.DeviceInfoRepository
import com.ganjianping.ak.sample.features.httpurlconnection.HttpURLConnectionRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { DeviceInfoRepository(androidContext()) }
    single { HttpURLConnectionRepository() }
}
