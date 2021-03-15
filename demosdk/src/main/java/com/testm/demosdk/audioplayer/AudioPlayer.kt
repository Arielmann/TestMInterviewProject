package com.testm.demosdk.audioplayer

import android.media.MediaPlayer
import android.util.Log
import com.testm.demosdk.model.AudioFileData
import com.testm.demosdk.utils.Utils
import java.io.File


object AudioPlayer {

    private val TAG = AudioPlayer::class.java.simpleName
    private val AUDIO_FILES_PATH = Utils.getAudioFilesParentDir().absolutePath
    private var mp: MediaPlayer? = null
    private var currentlyPlayedFile: AudioFileData? = null

    fun onAudioFileClicked(audioFileData: AudioFileData) {
        if (currentlyPlayedFile == audioFileData) {
            if (mp!= null && mp!!.isPlaying) {
                pause()
            }
            return
        } else {
            release()
            play(audioFileData)
        }
    }

    private fun play(audioFileData: AudioFileData) {
        //set up MediaPlayer

        try {
            Log.d(TAG, "Creating media player")
            mp = MediaPlayer()
            Log.d(TAG, "Setting data source for media player")
            mp!!.setDataSource(AUDIO_FILES_PATH + File.separator.toString() + audioFileData.name)
            Log.d(TAG, "Preparing media player")
            mp!!.prepare()
            Log.d(TAG, "Starting media player")
            mp!!.start()
            currentlyPlayedFile = audioFileData
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun pause() {
        try {
            Log.d(TAG, "Pausing media player")
            mp?.pause()
            currentlyPlayedFile = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun release() {
        try {
            Log.d(TAG, "Stopping media player")
            mp?.release()
            currentlyPlayedFile = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}