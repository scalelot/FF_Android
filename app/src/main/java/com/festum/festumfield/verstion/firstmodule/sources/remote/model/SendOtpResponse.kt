package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SendOtpResponse(

	@field:SerializedName("token")
	val token: String? = null

) : Serializable
