package com.testm.demosdk.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.zxing.integration.android.IntentIntegrator
import com.testm.demosdk.R
import com.testm.demosdk.audioplayer.AudioPlayer
import com.testm.demosdk.databinding.ActivitySdkMainBinding
import com.testm.demosdk.events.DemoSDKEvent
import com.testm.demosdk.viewmodel.SDKViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SDKMainActivity : AppCompatActivity() {

    companion object {
        val TAG: String = SDKMainActivity::class.java.simpleName
    }

    private lateinit var audioDataAdapter: AudioDataAdapter
    private lateinit var binding: ActivitySdkMainBinding
    private val sdkViewModel: SDKViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySdkMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAudioFilesList()
        setupDataObservation()
        startQRScan()
    }

    /**
    Initializing the QR scan process by opening the device's camera.
     */
    private fun startQRScan() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt(getString(R.string.scan_a_barcode))
        integrator.setCameraId(0) // Use a specific camera of the device
        integrator.setBeepEnabled(false)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        sdkViewModel.handleQRScanResults(requestCode, resultCode, data)
    }

    /**
     * Starts audio files data observation via the fragment's viewModel.
     * When the observer is activated, it's data will be displayed in the audio files list
     */
    private fun setupDataObservation() {
        sdkViewModel.audioFiles.observe(this, { audioFiles ->
            audioFiles?.let {
                Log.d(TAG, "Audio file data received from viewModel")
                audioDataAdapter.submitList(ArrayList(audioFiles))
            } ?: Log.w(TAG, "No audio data received")
        })

        observeErrorEvent(sdkViewModel.qrCodeScanErrorEvent, getString(R.string.error_qr_scan_failed))
        observeErrorEvent(sdkViewModel.audioDataListDownloadErrorEvent, getString(R.string.error_audio_data_fetch_failed))

    }

    private fun observeErrorEvent(errorEvent: MutableLiveData<DemoSDKEvent>,
                                  errorMsg: String = getString(R.string.error_unknown_failure)) {
        errorEvent.observe(this, { event ->
            event?.let {
                if (event.message.isNotBlank()) {
                    Toast.makeText(this, event.message, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                }
            } ?: Toast.makeText(this, getString(R.string.error_null_event), Toast.LENGTH_LONG)
                .show()
        })
    }

    /**
     * Sets the audio data list
     */
    private fun setupAudioFilesList() {
        audioDataAdapter = AudioDataAdapter(this)

        audioDataAdapter.onItemClickListener = { audioData ->
            Log.d(TAG, "Playing audio")
            AudioPlayer.play(audioData.name)
        }

        binding.audioDataRV.apply {
            adapter = audioDataAdapter
            layoutManager = LinearLayoutManager(this@SDKMainActivity)
        }
    }
}