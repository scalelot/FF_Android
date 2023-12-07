package com.festum.festumfield.verstion.firstmodule.sources.local.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ChatListBody(

	@field:SerializedName("limit")
	val limit: Int? = null,

	@field:SerializedName("to")
	val to: String? = null,

	@field:SerializedName("page")
	val page: Int? = null

) : Serializable
