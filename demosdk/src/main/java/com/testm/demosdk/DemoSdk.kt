package com.testm.demosdk

import android.app.Activity
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.testm.demosdk.view.SDKMainActivity


object DemoSdk {

    fun start(activity: Activity) {
        val intent = Intent(activity, SDKMainActivity::class.java)
        startActivity(activity, intent, null)
    }

}