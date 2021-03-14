package com.testm.interviewproject

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.testm.demosdk.DemoSdk


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.startBtn).setOnClickListener {
            DemoSdk.start(this)
        }

    }


}