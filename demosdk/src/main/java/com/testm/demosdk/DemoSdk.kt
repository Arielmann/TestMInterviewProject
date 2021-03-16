package com.testm.demosdk

import android.app.Activity
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.testm.demosdk.view.SDKMainActivity

/**
 * The Demo sdk's entry point
 */
object DemoSdk {

    /**
     * Starts the Demo sdk's process
     *
     * @param activity The client's caller activity
     */
    fun start(activity: Activity) {
        val intent = Intent(activity, SDKMainActivity::class.java)
        startActivity(activity, intent, null)
    }

}