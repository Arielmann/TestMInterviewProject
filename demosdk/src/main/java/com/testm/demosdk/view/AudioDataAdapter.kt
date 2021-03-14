package com.testm.demosdk.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.testm.demosdk.adapterhelpers.DefaultDiffUtilCallback
import com.testm.demosdk.databinding.VhAudioDataListBinding
import com.testm.demosdk.model.AudioFileData
import java.lang.ref.WeakReference

/**
 * Adapter for managing the display of the [AudioFileData]
 */
class AudioDataAdapter(context: Context) : androidx.recyclerview.widget.ListAdapter<AudioFileData, AudioDataAdapter.AudioDataViewHolder>(DefaultDiffUtilCallback<AudioFileData>()) {

    companion object {
        val TAG: String = AudioDataAdapter::class.simpleName!!
    }

    private val weakContext: WeakReference<Context> = WeakReference(context)
    lateinit var onItemClickListener: (stock: AudioFileData) -> Unit

    inner class AudioDataViewHolder(private val binding: VhAudioDataListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(audioFileData: AudioFileData) {
            binding.audioDataListViewHolderNameTV.text = audioFileData.name
            binding.root.setOnClickListener { onItemClickListener.invoke(audioFileData) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioDataViewHolder {
        val binding = VhAudioDataListBinding.inflate(LayoutInflater.from(weakContext.get()), parent, false)
        return AudioDataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AudioDataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}


