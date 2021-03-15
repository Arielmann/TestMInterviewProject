package com.testm.demosdk.viewmodel

import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.testm.demosdk.utils.Utils
import com.google.zxing.integration.android.IntentIntegrator
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

    val audioFiles: MutableLiveData<MutableMap<String, AudioFileData>> = MutableLiveData(mutableMapOf())
    val scanCanceledEvent = MutableLiveData<DemoSDKEvent>()
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
                scanCanceledEvent.postValue(DemoSDKEvent())
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

    /**
     * Downloads the audio files from the URLs located in the audio data list
     *
     * @param filesData Data list of the target files
     */
    private fun downloadAudioFiles(filesData: List<AudioFileData>) {
        Log.d(TAG, "downloadAudioFiles")

        filesData.forEach { audioFileData ->
            viewModelScope.launch(Dispatchers.IO) {

                audioDataRepository.downloadAudioFile(audioFileData, object :
                    NetworkCallback<ResponseBody> {
                    override fun onSuccess(result: ResponseBody) {
                        viewModelScope.launch(Dispatchers.IO) {
                            val isFileSaved = Utils.saveAudioFile(result, audioFileData.name)
                            if (isFileSaved && !audioFiles.value!!.containsKey(audioFileData.url)) {
                                notifyNewAudioFileAvailable(audioFileData)
                            }
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
        Log.d(TAG, "${audioFileData.name} will be added to playlist list")
        audioFiles.value!![audioFileData.url] = audioFileData
        audioFiles.postValue(audioFiles.value!!)
    }

}