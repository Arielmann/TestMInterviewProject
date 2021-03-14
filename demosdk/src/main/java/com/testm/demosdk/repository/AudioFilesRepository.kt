package com.testm.demosdk.repository

import android.accounts.NetworkErrorException
import android.util.Log
import com.example.maytronicstestapp.crossapplication.utils.Utils.isNetworkAvailable
import com.testm.demosdk.app.DemoSDKApp
import com.testm.demosdk.model.AudioData
import com.testm.demosdk.network.AudioFilesNetworkService
import com.testm.demosdk.network.NetworkCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class AudioFilesRepository @Inject constructor(private val audioFilesNetworkService: AudioFilesNetworkService) {

    companion object {
        private val TAG: String? = AudioFilesRepository::class.simpleName
    }

    private suspend fun <T> startOperationIfNetworkAvailable(operation: suspend () -> T) {
        val context = DemoSDKApp.context
        if (isNetworkAvailable(context)) {
            Log.d(TAG, "Network available, starting network operation")
            operation.invoke()
        } else {
            throw NetworkErrorException("No network connection")
        }
    }

    suspend fun fetchAudioData(qrURL: String, networkCallback: NetworkCallback<List<AudioData>>) {

        startOperationIfNetworkAvailable {
            Log.d(TAG, "Executing audio data fetch request")
            val call = audioFilesNetworkService.fetchAudioData(qrURL)

            call.enqueue(object : Callback<List<AudioData>> {
                override fun onResponse(call: Call<List<AudioData>>, response: Response<List<AudioData>>) {
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

                override fun onFailure(call: Call<List<AudioData>>, t: Throwable) {
                    Log.e(TAG, "Audio data fetch error: " + t.message)
                    t.printStackTrace()
                    networkCallback.onFailure("Audio fetch request failed")
                }
            })

        }

    }
}
