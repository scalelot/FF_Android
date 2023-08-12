package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductItem(

	@field:SerializedName("offer")
	val offer: String? = null,

	@field:SerializedName("subCategory")
	val subCategory: String? = null,

	@field:SerializedName("images")
	val images: List<String?>? = null,

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("itemCode")
	val itemCode: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("category")
	val category: String? = null

) : Serializable
