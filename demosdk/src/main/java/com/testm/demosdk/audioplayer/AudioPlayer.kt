package com.testm.demosdk.audioplayer

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import com.testm.demosdk.app.DemoSDKApp
import com.testm.demosdk.model.AudioFileData
import com.testm.demosdk.utils.Utils
import java.io.File


object AudioPlayer {

    private val AUDIO_FILES_PATH = Utils.getAudioFilesParentDir().absolutePath

    fun play(audioFileName: String) {
        //set up MediaPlayer
        val mp = MediaPlayer()
        try {
            mp.setDataSource(AUDIO_FILES_PATH + File.separator.toString() + audioFileName)
            mp.prepare()
            mp.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isFilePlayable(audioFileData : AudioFileData): Boolean {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(DemoSDKApp.context, Uri.parse(audioFileData.localFilePath))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return false
        }
        val hasAudio = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO)
        retriever.close()
        return "yes" == hasAudio
    }

}