package com.festum.festumfield.verstion.firstmodule.sources.local.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SendRequestBody(

	@field:SerializedName("receiverid")
	val receiverId: String? = null,

	@field:SerializedName("message")
	val message: String? = null
) : Serializable
