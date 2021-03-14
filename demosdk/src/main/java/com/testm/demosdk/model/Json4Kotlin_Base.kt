package com.testm.demosdk.model

import com.google.gson.annotations.SerializedName
import com.testm.demosdk.adapterhelpers.ObjectIdentifier

/**
 * A model representing data about an audio file within a remote server
 */
data class AudioData (
	@SerializedName("id") val id : Int,
	@SerializedName("name") val name : String,
	@SerializedName("url") val url : String
)  : ObjectIdentifier{

	override fun getUniqueProperty(): String {
		return id.toString()
	}

}