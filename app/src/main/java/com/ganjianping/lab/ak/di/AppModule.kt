package com.ganjianping.lab.ak.di

import com.ganjianping.lab.ak.features.deviceinfo.data.DeviceInfoRepository
import com.ganjianping.lab.ak.features.httpurlconnection.HttpURLConnectionRepository
import com.ganjianping.lab.ak.integration.firebase.firebaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    includes(firebaseModule)
    single { DeviceInfoRepository(androidContext()) }
    single { HttpURLConnectionRepository() }
}
