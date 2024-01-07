package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PhonebookResponse(

	@field:SerializedName("contact_no")
	val contactNo: String? = null,

	@field:SerializedName("isFFuser")
	val isFFuser: Boolean? = null

) : Serializable
