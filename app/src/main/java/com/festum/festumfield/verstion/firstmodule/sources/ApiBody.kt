package com.festum.festumfield.verstion.firstmodule.sources

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ApiBody(

	@field:SerializedName("Status")
	val status: Int? = null,

	@field:SerializedName("IsSuccess")
	val isSuccess: Boolean? = null,

	@field:SerializedName("Message")
	val message: String? = null,

	@field:SerializedName("Data")
	val data: Int? = null

) : Serializable
