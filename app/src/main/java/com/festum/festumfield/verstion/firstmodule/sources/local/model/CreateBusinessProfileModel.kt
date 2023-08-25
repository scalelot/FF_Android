package com.festum.festumfield.verstion.firstmodule.sources.local.model

import com.google.gson.annotations.SerializedName

data class CreateBusinessProfileModel(

	@field:SerializedName("subCategory")
	val subCategory: String? = null,

	@field:SerializedName("interestedSubCategory")
	val interestedSubCategory: String? = null,

	@field:SerializedName("interestedCategory")
	val interestedCategory: String? = null,

	@field:SerializedName("latitude")
	val latitude: Any? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("category")
	val category: String? = null,

	@field:SerializedName("longitude")
	val longitude: Any? = null
)
