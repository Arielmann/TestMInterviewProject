package com.testm.demosdk.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.zxing.integration.android.IntentIntegrator
import com.testm.demosdk.R
import com.testm.demosdk.databinding.ActivitySdkMainBinding
import com.testm.demosdk.events.DemoSDKEvent
import com.testm.demosdk.viewmodel.SDKViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main activity of the sdk. Responsible for starting the barcode scan process
 * and displaying its results
 */
@AndroidEntryPoint
class SDKMainActivity : AppCompatActivity() {

    companion object {
        val TAG: String = SDKMainActivity::class.java.simpleName
    }

    private lateinit var audioDataAdapter: AudioDataAdapter
    private lateinit var binding: ActivitySdkMainBinding
    private val sdkViewModel: SDKViewModel by viewModels()

    private val onRestartClickListener = View.OnClickListener { startQRScan() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySdkMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAudioFilesList()
        setupDataObservation()
        binding.restartProcessIV.setOnClickListener(onRestartClickListener)
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
        showProgressionUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        sdkViewModel.handleQRScanResults(requestCode, resultCode, data)
        binding.progressBar.show()
    }

    /**
     * Starts audio files data observation via the fragment's viewModel.
     * When the observer is activated, it's data will be displayed in the audio files list
     */
    private fun setupDataObservation() {
        sdkViewModel.audioFiles.observe(this, { audioFiles ->
            audioFiles?.let {
                Log.d(TAG, "Audio file data received from viewModel")
                audioDataAdapter.submitList(audioFiles.values.toList())
                hideProgressionUI()
            } ?: Log.w(TAG, "No audio data received")
        })

        observeFailureScenario(sdkViewModel.qrCodeScanErrorEvent, getString(R.string.error_qr_scan_failed))
        observeFailureScenario(sdkViewModel.audioDataListDownloadErrorEvent, getString(R.string.error_audio_data_fetch_failed))
        observeFailureScenario(sdkViewModel.scanCanceledEvent, getString(R.string.event_message_scan_cancelled))
    }

    /**
     * Displaying the title and progressbar and hiding any error messages\views
     */
    private fun showProgressionUI() {
        binding.progressBar.show()
        binding.restartProcessIV.visibility = View.INVISIBLE
        binding.errorTV.visibility = View.INVISIBLE
        binding.titleTV.visibility = View.VISIBLE
    }

    /**
     * Hiding the progressbar and hiding any error messages\views
     */
    private fun hideProgressionUI() {
        binding.progressBar.hide()
        binding.errorTV.visibility = View.INVISIBLE
        binding.titleTV.visibility = View.VISIBLE
    }


    /**
     * Defining the UI display after an unsuccessful scenario (not necessarily an error)
     *
     * @param event The occurring event
     * @param message Message to be displayed to the user
     */
    private fun observeFailureScenario(event: MutableLiveData<DemoSDKEvent>,
                                       message: String = getString(R.string.error_unknown_failure)) {
        event.observe(this, { result ->
            result?.let {
                if (result.message.isNotBlank()) {
                    onDataDisplayRequestFailed(result.message)
                } else {
                    onDataDisplayRequestFailed(message)
                }
            } ?: onDataDisplayRequestFailed(getString(R.string.error_null_event))
        })
    }

    /**
     * Sets the audio data list
     */
    private fun setupAudioFilesList() {
        audioDataAdapter = AudioDataAdapter(this)
        audioDataAdapter.setHasStableIds(true)
        sdkViewModel.audioPlayer.onAudioPlayingCompletedListener = audioDataAdapter.onAudioPlayingCompletedListener

        audioDataAdapter.onItemClickListener = { audioData ->
            sdkViewModel.audioPlayer.onAudioFileClicked(audioData)
        }

        binding.audioDataRV.apply {
            adapter = audioDataAdapter
            layoutManager = LinearLayoutManager(this@SDKMainActivity)
        }
    }

    /**
     * Displayed whenever the image request operation ends with an error
     */
    private fun onDataDisplayRequestFailed(reason: String) {
        Log.d(TAG, reason)
        binding.progressBar.hide()
        binding.errorTV.text = reason
        binding.errorTV.visibility = View.VISIBLE
        binding.titleTV.visibility = View.INVISIBLE
        binding.restartProcessIV.visibility = View.VISIBLE
        binding.restartProcessIV.setImageResource(R.drawable.ic_baseline_refresh_gray_48)
    }
}