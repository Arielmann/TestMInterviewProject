package com.testm.demosdk.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.testm.demosdk.app.DemoSDKApp
import com.testm.demosdk.model.AudioFileData
import okhttp3.ResponseBody
import java.io.*

object Utils {

    private val TAG = Utils::class.simpleName

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


    fun saveAudioFile(body: ResponseBody, audioFileData: AudioFileData): String {
        val parentDir = getAudioFilesParentDir()
        val audioFile = File(parentDir, audioFileData.name)

        var input: InputStream? = null
        try {
            input = body.byteStream()
            val fos = FileOutputStream(audioFile)
            fos.use { output ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
            Log.d(TAG, "Audio file ${audioFileData.name} saved in path: ${audioFile.absolutePath}")
            return audioFile.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        } finally {
            input?.close()
        }
        return ""
    }

    fun getAudioFilesParentDir(): File {
        val parentDir = File(DemoSDKApp.context.cacheDir.toString() + "/Audio_Files/")
        if (!parentDir.exists()) {
            parentDir.mkdirs()
        }
        return parentDir
    }

}
