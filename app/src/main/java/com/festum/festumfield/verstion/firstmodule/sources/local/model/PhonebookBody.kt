package com.festum.festumfield.verstion.firstmodule.sources.local.model

import com.google.gson.annotations.SerializedName

data class PhonebookBody (

	@field:SerializedName("contactNumbers")
	val contactNumbers: ArrayList<String?>? = null

)
