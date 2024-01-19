package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CallId(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("isGroupCall")
	val isGroupCall: Boolean? = null,

	@field:SerializedName("isAudioCall")
	val isAudioCall: Boolean? = null,

	@field:SerializedName("isVideoCall")
	val isVideoCall: Boolean? = null,

	@field:SerializedName("members")
	val members: List<MembersItem?>? = null,

	@field:SerializedName("__v")
	val v: Int? = null,

	@field:SerializedName("from")
	val from: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("to")
	val to: String? = null,

	@field:SerializedName("initiatedBy")
	val initiatedBy: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null

) : Serializable


