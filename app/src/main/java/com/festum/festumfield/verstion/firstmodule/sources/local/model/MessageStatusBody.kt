package com.festum.festumfield.verstion.firstmodule.sources.local.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MessageStatusBody(

	@field:SerializedName("messageid")
	val messageid: String? = null

) : Serializable
