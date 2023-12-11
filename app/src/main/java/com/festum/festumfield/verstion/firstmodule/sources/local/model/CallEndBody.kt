package com.festum.festumfield.verstion.firstmodule.sources.local.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CallEndBody (

	@field:SerializedName("callid")
	val callId: String? = null

) : Serializable
