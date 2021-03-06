package com.testm.demosdk.adapterhelpers

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

/**
 * Default DiffUtil callback for lists adapters.
 * The adapter utilizes the fact that all models in the app implement the [ObjectIdentifier] interfaces, so
 * it uses it in order to compare the unique name of each model for `areItemsTheSame` function.
 * As for areContentsTheSame we utilize the fact that Kotlin Data Class implements for us the equals between
 * all fields, so use the equals() method to compare one object to another.
 */
@Suppress("UnnecessaryVariable") class DefaultDiffUtilCallback<T : ObjectIdentifier> : DiffUtil.ItemCallback<T>() {

    companion object {
        @Suppress("unused") private val TAG = DefaultDiffUtilCallback::class.qualifiedName
    }

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        val areSame = oldItem.getUniqueProperty() == newItem.getUniqueProperty()
        return areSame
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        val areSame = oldItem == newItem
        return areSame
    }
}