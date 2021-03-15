package com.testm.demosdk.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.testm.demosdk.R
import com.testm.demosdk.adapterhelpers.DefaultDiffUtilCallback
import com.testm.demosdk.databinding.VhAudioDataListBinding
import com.testm.demosdk.model.AudioFileData
import java.lang.ref.WeakReference

/**
 * Adapter for managing the display of the [AudioFileData]
 */
class AudioDataAdapter(context: Context) :
    androidx.recyclerview.widget.ListAdapter<AudioFileData, AudioDataAdapter.AudioDataViewHolder>(DefaultDiffUtilCallback<AudioFileData>()) {

    companion object {
        val TAG: String = AudioDataAdapter::class.simpleName!!
    }

    private var currentlyPlayingViewHolder: AudioDataViewHolder? = null
    private var currentlyPlayedAudio: AudioFileData? = null
    private val weakContext: WeakReference<Context> = WeakReference(context)
    lateinit var onItemClickListener: (stock: AudioFileData) -> Unit

    inner class AudioDataViewHolder(private val binding: VhAudioDataListBinding) :
        RecyclerView.ViewHolder(binding.root) {

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
                    currentlyPlayingViewHolder = null
                    currentlyPlayedAudio = null
                    binding.audioDataViewHolderPlayPauseIV.setImageResource(R.drawable.ic_play_black)
                }
                onItemClickListener.invoke(audioFileData)
            }
        }

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


