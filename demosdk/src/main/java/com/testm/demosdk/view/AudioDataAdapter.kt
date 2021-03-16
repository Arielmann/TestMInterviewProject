package com.testm.demosdk.view

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.testm.demosdk.R
import com.testm.demosdk.adapterhelpers.DefaultDiffUtilCallback
import com.testm.demosdk.databinding.VhAudioDataListBinding
import com.testm.demosdk.model.AudioFileData
import java.lang.ref.WeakReference

/**
 * Adapter for managing the display of the [AudioFileData]
 */
class AudioDataAdapter(context: Context) : ListAdapter<AudioFileData, AudioDataAdapter.AudioDataViewHolder>(DefaultDiffUtilCallback<AudioFileData>()) {

    companion object {
        val TAG: String = AudioDataAdapter::class.simpleName!!
    }

    private var currentlyPlayingViewHolder: AudioDataViewHolder? = null
    private var currentlyPlayedAudio: AudioFileData? = null
    private val weakContext: WeakReference<Context> = WeakReference(context)
    lateinit var onItemClickListener: (audioFileData: AudioFileData) -> Unit

    val onAudioPlayingCompletedListener: MediaPlayer.OnCompletionListener = MediaPlayer.OnCompletionListener {
        onAudioStoppedPlaying()
    }

    /**
     * A view holder for managing the screen display of a single [AudioFileData]
     */
    inner class AudioDataViewHolder(val binding: VhAudioDataListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(audioFileData: AudioFileData, holder: AudioDataViewHolder) {
            binding.audioDataListViewHolderNameTV.text = audioFileData.name
            binding.audioDataListViewHolderURLTV.text = audioFileData.url

            if (audioFileData == currentlyPlayedAudio) {
                binding.audioDataViewHolderPlayPauseIV.setImageResource(R.drawable.ic_pause_black)
                currentlyPlayingViewHolder = holder
            }

            binding.root.setOnClickListener {
                if (audioFileData != currentlyPlayedAudio) {
                    currentlyPlayingViewHolder?.binding?.audioDataViewHolderPlayPauseIV?.setImageResource(R.drawable.ic_play_black)
                    binding.audioDataViewHolderPlayPauseIV.setImageResource(R.drawable.ic_pause_black)
                    currentlyPlayingViewHolder = holder
                    currentlyPlayedAudio = audioFileData
                } else {
                    onAudioStoppedPlaying()
                }
                onItemClickListener.invoke(audioFileData)
            }
        }
    }

    /**
     * Called whenever a audio file pauses or finished playing with no interruption.
     * If throughout its playing time a new audio file will be asked to play, the method will not be called
     */
    private fun onAudioStoppedPlaying() {
        Log.d(TAG, "Setting viewHolder to 'Not playing' mode ")
        currentlyPlayingViewHolder?.binding?.audioDataViewHolderPlayPauseIV?.setImageResource(R.drawable.ic_play_black)
        currentlyPlayedAudio = null
        currentlyPlayingViewHolder = null
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioDataViewHolder {
        val binding =
            VhAudioDataListBinding.inflate(LayoutInflater.from(weakContext.get()), parent, false)
        return AudioDataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AudioDataViewHolder, position: Int) {
        holder.bind(getItem(position), holder)
    }
}


