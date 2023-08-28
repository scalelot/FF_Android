package com.festum.festumfield.verstion.firstmodule.sources.local.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FindFriendsBody(

	@field:SerializedName("search")
	val search: String? = null,

	@field:SerializedName("latitude")
	val latitude: Any? = null,

	@field:SerializedName("longitude")
	val longitude: Any? = null

) : Serializable
