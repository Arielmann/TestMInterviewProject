package com.testm.demosdk.viewmodel

import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.testm.demosdk.utils.Utils
import com.google.zxing.integration.android.IntentIntegrator
import com.testm.demosdk.audioplayer.AudioPlayer
import com.testm.demosdk.events.DemoSDKEvent
import com.testm.demosdk.model.AudioFileData
import com.testm.demosdk.network.NetworkCallback
import com.testm.demosdk.repository.AudioFilesRepository
import com.testm.demosdk.view.SDKMainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class SDKViewModel @Inject constructor(private val audioDataRepository: AudioFilesRepository) :
    ViewModel() {

    companion object {
        private val TAG: String? = SDKViewModel::class.java.simpleName
    }

    val audioFiles: MutableLiveData<List<AudioFileData>> = MutableLiveData(mutableListOf())
    val qrCodeScanErrorEvent = MutableLiveData<DemoSDKEvent>()
    val audioDataListDownloadErrorEvent = MutableLiveData<DemoSDKEvent>()

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
                requestAudioDataList(qruUrl)
            } else {
                Log.d(SDKMainActivity.TAG, "Scan cancelled")
                qrCodeScanErrorEvent.postValue(DemoSDKEvent())
            }
        }
    }

    /**
     * Starting a download process for the audio data found in the QR barcode's response URL
     */
    private fun requestAudioDataList(qruUrl: String) {
        Log.d(TAG, "Starting audio data download")
        viewModelScope.launch(Dispatchers.IO) {
            audioDataRepository.fetchAudioDataList(qruUrl, object :
                NetworkCallback<List<AudioFileData>> {
                override fun onSuccess(result: List<AudioFileData>) {
//                    audioFiles.postValue(result)
                    downloadAudioFiles(result)
                }

                override fun onFailure(error: String) {
                    audioDataListDownloadErrorEvent.postValue(DemoSDKEvent())
                }
            })
        }
    }

    private fun downloadAudioFiles(result: List<AudioFileData>) {
        Log.d(TAG, "downloadAudioFiles")

        result.forEach { audioFileData ->
            viewModelScope.launch(Dispatchers.IO) {
                audioDataRepository.downloadAudioFile(audioFileData, object :
                    NetworkCallback<ResponseBody> {
                    override fun onSuccess(result: ResponseBody) {
                        audioFileData.localFilePath = Utils.saveAudioFile(result, audioFileData)
                        if (isAudioFileValid(audioFileData)) {
                            notifyNewAudioFileAvailable(audioFileData)
                        }
                    }

                    override fun onFailure(error: String) {
                        //View should not be updated about failure for this event therefore repository logs all failures
                    }
                })
            }
        }
    }

    private fun notifyNewAudioFileAvailable(audioFileData: AudioFileData) {
        val updatedAudioFilesList: MutableList<AudioFileData> = mutableListOf()
        updatedAudioFilesList.addAll(audioFiles.value!!)
        updatedAudioFilesList.add(audioFileData)
        audioFiles.postValue(updatedAudioFilesList)
    }

    private fun isAudioFileValid(audioFileData: AudioFileData): Boolean {
        if (audioFileData.localFilePath.isEmpty()) {
            Log.w(TAG, "Audio file is invalid because it isn't saved correctly on device")
            return false
        }

        /*if (AudioPlayer.isFilePlayable(audioFileData)) {
            Log.w(TAG, "Audio file is invalid because it isn't playable")
            return false
        }*/

        return true
    }

}