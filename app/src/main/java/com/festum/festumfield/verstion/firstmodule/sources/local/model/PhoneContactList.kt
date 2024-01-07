package com.festum.festumfield.verstion.firstmodule.sources.local.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PhoneContactList(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("number")
	val number: String? = null,

	@field:SerializedName("isFFuser")
	val isFFuser: Boolean? = null,

) : Serializable
