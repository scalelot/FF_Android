package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SendMessageContent(

	@field:SerializedName("product")
	val product: SendProduct? = null,

	@field:SerializedName("text")
	val text: SendText? = null,

	@field:SerializedName("media")
	val media: SendMedia? = null

) : Serializable
