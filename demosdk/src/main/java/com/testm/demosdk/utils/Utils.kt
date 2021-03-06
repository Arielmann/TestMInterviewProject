package com.testm.demosdk.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.testm.demosdk.app.DemoSDKApp
import okhttp3.ResponseBody
import java.io.File


object Utils {

    private val TAG = Utils::class.simpleName

    /**
     * Checks whether a internet connection is available
     *
     * @param context A context for preparing the operation
     *
     * @return tre if an internet connection is available
     */
    @Suppress("DEPRECATION")
    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    /**
     * Saving an audio file to the device's cache
     *
     * @param body The body containing the file's data
     * @param fileName Name of the saved file
     *
     * @return true if the file was successfully saved
     */
    fun saveAudioFile(body: ResponseBody, fileName: String): Boolean {

        try {

            DemoSDKApp.context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(body.bytes())
            }
            Log.d(TAG, "Audio file $fileName saved in path: $fileName")
            return true

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        Log.e(TAG, "Error in file saving for file $fileName")
        return false
    }


    /**
     * Obtains the dir where the audio files are to be saved
     *
     * @return A dir where the audio files are to be saved
     */
    fun getAudioFilesParentDir(): File {
        return DemoSDKApp.context.filesDir
    }

}
