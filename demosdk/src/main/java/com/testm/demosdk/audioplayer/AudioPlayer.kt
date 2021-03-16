package com.testm.demosdk.audioplayer

import android.media.MediaPlayer
import android.util.Log
import com.testm.demosdk.model.AudioFileData
import com.testm.demosdk.utils.Utils
import java.io.File


/**
 * Responsible for playing provided audio files
 */
class AudioPlayer {

    companion object {
        private val TAG = AudioPlayer::class.java.simpleName
        private val AUDIO_FILES_PATH = Utils.getAudioFilesParentDir().absolutePath
    }

    private var mp: MediaPlayer? = null
    private var currentlyPlayedFile: AudioFileData? = null
    var onAudioPlayingCompletedListener: MediaPlayer.OnCompletionListener? = null
        set(value) {
            field = MediaPlayer.OnCompletionListener {
                Log.d(TAG, "Audio file finished")
                currentlyPlayedFile = null
                value?.onCompletion(mp)
            }
        }

    /**
     * Called whenever a user clicks on an audio file.
     * The player will decide whether the file should be played or opposed according to its state
     *
     * @param audioFileData Data regarding the desired file
     */
    fun onAudioFileClicked(audioFileData: AudioFileData) {
        if (currentlyPlayedFile == audioFileData) {
            if (mp != null && mp!!.isPlaying) {
                pause()
            } else {
                play(audioFileData)
            }
            return
        } else {
            release()
            play(audioFileData)
        }
    }


    /**
     * Playing a given [AudioFileData]
     *
     * @param audioFileData Data regarding the file for playing
     */
    private fun play(audioFileData: AudioFileData) {
        //set up MediaPlayer

        try {
            if (currentlyPlayedFile != audioFileData) {
                Log.d(TAG, "A new file will be played")
                Log.d(TAG, "Creating media player")
                mp = MediaPlayer()
                Log.d(TAG, "Setting data source for media player")
                mp!!.setDataSource(AUDIO_FILES_PATH + File.separator.toString() + audioFileData.name)
                Log.d(TAG, "Preparing media player")
                mp!!.prepare()
            } else {
                Log.d(TAG, "Replaying the current audio file from its current pausing point")
            }
            Log.d(TAG, "Starting media player")
            mp!!.start()
            mp!!.setOnCompletionListener(onAudioPlayingCompletedListener)

            currentlyPlayedFile = audioFileData
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Pausing the currently played [AudioFileData]
     */
    private fun pause() {
        try {
            Log.d(TAG, "Pausing media player")
            mp?.pause()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Releasing the media player
     */
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