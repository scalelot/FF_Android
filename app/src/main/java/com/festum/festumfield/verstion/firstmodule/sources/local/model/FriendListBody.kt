package com.festum.festumfield.verstion.firstmodule.sources.local.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FriendListBody(

	@field:SerializedName("search")
	val search: String? = null,

	@field:SerializedName("isGroup")
	val isGroup: Boolean? = null,

	@field:SerializedName("limit")
	val limit: Int? = null,

	@field:SerializedName("page")
	val page: Int? = null

) : Serializable
