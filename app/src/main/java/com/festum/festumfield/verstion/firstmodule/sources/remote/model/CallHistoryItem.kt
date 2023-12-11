package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CallHistoryItem (

	@field:SerializedName("isGroupCall")
	val isGroupCall: Boolean? = null,

	@field:SerializedName("isVideoCall")
	val isVideoCall: Boolean? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("callStartedAt")
	val callStartedAt: Long? = null,

	@field:SerializedName("__v")
	val v: Int? = null,

	@field:SerializedName("callEndedAt")
	val callEndedAt: Long? = null,

	@field:SerializedName("from")
	val from: From? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("to")
	val to: To? = null,

	@field:SerializedName("id")
	val mainId: String? = null,

	@field:SerializedName("initiatedBy")
	val initiatedBy: InitiatedBy? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null

) : Serializable





