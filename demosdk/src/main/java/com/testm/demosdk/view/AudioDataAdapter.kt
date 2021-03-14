package com.testm.demosdk.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.testm.demosdk.adapterhelpers.DefaultDiffUtilCallback
import com.testm.demosdk.databinding.VhAudioDataListBinding
import com.testm.demosdk.model.AudioData
import java.lang.ref.WeakReference

/**
 * Adapter for managing the display of the [AudioData]
 */
class AudioDataAdapter(context: Context) : androidx.recyclerview.widget.ListAdapter<AudioData, AudioDataAdapter.AudioDataViewHolder>(DefaultDiffUtilCallback<AudioData>()) {

    companion object {
        val TAG: String = AudioDataAdapter::class.simpleName!!
    }

    private val weakContext: WeakReference<Context> = WeakReference(context)
    lateinit var onItemClickListener: (stock: AudioData) -> Unit

    inner class AudioDataViewHolder(private val binding: VhAudioDataListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(audioData: AudioData) {
            binding.audioDataListViewHolderNameTV.text = audioData.name
            binding.root.setOnClickListener { onItemClickListener.invoke(audioData) }
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


