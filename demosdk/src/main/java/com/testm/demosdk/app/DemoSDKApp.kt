package com.testm.demosdk.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DemoSDKApp : Application() {

    companion object {
        @Suppress("unused") val APP_TAG = DemoSDKApp::class.simpleName
        @SuppressLint("StaticFieldLeak") //It's the app's context. It should move around freely and get collected when the app gets terminated
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}