package com.festum.festumfield.verstion.firstmodule.sources.local.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CreateGroupBody(

	@field:SerializedName("groupid")
	val groupid: String? = null,

	@field:SerializedName("members")
	val members: ArrayList<String>? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("profileimage")
	val profileimage: String? = null

) : Serializable
