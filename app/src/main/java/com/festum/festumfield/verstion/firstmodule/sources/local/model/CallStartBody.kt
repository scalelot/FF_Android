package com.festum.festumfield.verstion.firstmodule.sources.local.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CallStartBody (

	@field:SerializedName("from")
	val from: String? = null,

	@field:SerializedName("to")
	val to: String? = null,

	@field:SerializedName("isVideoCall")
	val isVideoCall: Boolean? = null,

	@field:SerializedName("isGroupCall")
	val isGroupCall: Boolean? = null,

	@field:SerializedName("isAudioCall")
	val isAudioCall: Boolean? = null,

	@field:SerializedName("status")
	val status: String? = null

) : Serializable
