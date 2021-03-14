package com.testm.demosdk.viewmodel

import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.integration.android.IntentIntegrator
import com.testm.demosdk.events.DemoSDKEvent
import com.testm.demosdk.model.AudioData
import com.testm.demosdk.network.NetworkCallback
import com.testm.demosdk.repository.AudioFilesRepository
import com.testm.demosdk.view.SDKMainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SDKViewModel @Inject constructor(private val audioDataRepository: AudioFilesRepository) : ViewModel() {

    companion object {
        private val TAG: String? = SDKViewModel::class.java.simpleName
    }

    val audioFiles: MutableLiveData<List<AudioData>> = MutableLiveData()
    val qrCodeScanErrorEvent = MutableLiveData<DemoSDKEvent>()
    val audioDataDownloadErrorEvent = MutableLiveData<DemoSDKEvent>()

    /**
     * Parsing the results obtained from the QR barcode scan.
     * Upon a successful parsing, the download of the data contained withing the json files will start immediately
     */
    fun handleQRScanResults(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            val qruUrl = result.contents
            if (qruUrl != null) {
                Log.d(TAG, "Scan successful. Result URL: $qruUrl")
                Log.d(TAG, "Starting audio data download")
                downloadAudioData(qruUrl)
            } else {
                Log.d(SDKMainActivity.TAG, "Scan cancelled")
                qrCodeScanErrorEvent.postValue(DemoSDKEvent())
            }
        }
    }

    /**
     * Starting a download process for the audio data found in the QR barcode's response URL
     */
    private fun downloadAudioData(qruUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            audioDataRepository.fetchAudioData(qruUrl, object : NetworkCallback<List<AudioData>> {
                override fun onSuccess(result: List<AudioData>) {
                    audioFiles.postValue(result)
                }

                override fun onFailure(message: String) {
                    audioDataDownloadErrorEvent.postValue(DemoSDKEvent())
                }
            })
        }
    }
}