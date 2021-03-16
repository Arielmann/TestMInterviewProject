package com.testm.demosdk.repository

import android.accounts.NetworkErrorException
import android.util.Log
import com.testm.demosdk.utils.Utils.isNetworkAvailable
import com.testm.demosdk.app.DemoSDKApp
import com.testm.demosdk.model.AudioFileData
import com.testm.demosdk.network.AudioFilesNetworkService
import com.testm.demosdk.network.NetworkCallback
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject


class AudioFilesRepository @Inject constructor(private val audioFilesNetworkService: AudioFilesNetworkService) {

    companion object {
        private val TAG: String? = AudioFilesRepository::class.simpleName
    }

    /**
     * Starts an operation if network is available for the client. Otherwise throws a [NetworkErrorException]
     *
     * @param operation The desired operation for execution
     *
     * @throws NetworkErrorException if network is not available
     */
    private suspend fun <T> startOperationIfNetworkAvailable(operation: suspend () -> T) {
        val context = DemoSDKApp.context
        if (isNetworkAvailable(context)) {
            Log.d(TAG, "Network available, starting network operation")
            operation.invoke()
        } else {
            throw NetworkErrorException("No network connection")
        }
    }

    /**
     * Downloading the data regarding the entire list of files
     *
     * @param qrURL URL of the desired data
     * @param networkCallback a callback for updating the operation's status
     */
    suspend fun fetchAudioDataList(qrURL: String, networkCallback: NetworkCallback<List<AudioFileData>>) {

        try {
            startOperationIfNetworkAvailable {
                Log.d(TAG, "Executing audio data fetch request")
                val call = audioFilesNetworkService.fetchAudioData(qrURL)

                call.enqueue(object : Callback<List<AudioFileData>> {
                    override fun onResponse(call: Call<List<AudioFileData>>, response: Response<List<AudioFileData>>) {
                        Log.d(TAG, "Audio data fetch response: $response")
                        if (response.body() != null && response.isSuccessful) {
                            if (response.body() != null) {
                                Log.d(TAG, "Audio data download successful")
                                networkCallback.onSuccess(response.body()!!)
                                return
                            }
                        }
                        Log.d(TAG, "Audio data download fetched a bad response ")
                        networkCallback.onFailure("Audio fetch request failed")
                    }

                    override fun onFailure(call: Call<List<AudioFileData>>, t: Throwable) {
                        Log.e(TAG, "Audio data fetch error: " + t.message)
                        t.printStackTrace()
                        networkCallback.onFailure("Audio fetch request failed")
                    }
                })

            }
        } catch (e: Exception) {
            val errorMsg = "fetchAudioDataListL: An error has occurred but was caught not caught within the retrofit designated failure mechanism"
            Log.e(TAG, errorMsg)
            e.printStackTrace()
            networkCallback.onFailure(errorMsg)
        }

    }

    /**
     * Downloads a single audio file
     *
     * @param audioFileData Data regarding the target file for download
     * @param networkCallback a callback for updating the operation's status
     */
    suspend fun downloadAudioFile(audioFileData: AudioFileData, networkCallback: NetworkCallback<ResponseBody>) {
        try {
            Log.d(TAG, "Downloading audio file: ${audioFileData.name}")
            val response: Response<ResponseBody>? =
                audioFilesNetworkService.downloadFile(audioFileData.url)

            if (response?.body() == null) {
                Log.e(TAG, "Download file error. Null response")
                networkCallback.onFailure("Null response from server for file ${audioFileData.name}")
                return
            }

            if (response.isSuccessful) {
                Log.d(TAG, "Audio file ${audioFileData.name} downloaded successfully")
                networkCallback.onSuccess(response.body()!!)
            } else {
                Log.e(TAG, "Audio file ${audioFileData.name} download error: " + response.errorBody()
                    .toString())
                networkCallback.onFailure(response.errorBody().toString())
            }
        } catch (e: Exception) {
            val errorMsg = "downloadAudioFile: An error has occurred but was caught not caught within the retrofit designated failure mechanism"
            Log.e(TAG, errorMsg)
            e.printStackTrace()
            networkCallback.onFailure(errorMsg)
        }
    }
}
