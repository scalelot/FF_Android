package com.festum.festumfield.verstion.firstmodule.sources.local.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ChatUserBody (

	@field:SerializedName("friendid")
	val friendid: String? = null

) : Serializable
