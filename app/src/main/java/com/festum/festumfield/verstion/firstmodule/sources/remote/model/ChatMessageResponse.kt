package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName

data class ChatMessageResponse(

	@field:SerializedName("hasPrevPage")
	val hasPrevPage: Boolean? = null,

	@field:SerializedName("docs")
	val docs: List<DocsItem?>? = null,

	@field:SerializedName("hasNextPage")
	val hasNextPage: Boolean? = null,

	@field:SerializedName("pagingCounter")
	val pagingCounter: Int? = null,

	@field:SerializedName("nextPage")
	val nextPage: Any? = null,

	@field:SerializedName("limit")
	val limit: Int? = null,

	@field:SerializedName("totalPages")
	val totalPages: Int? = null,

	@field:SerializedName("prevPage")
	val prevPage: Any? = null,

	@field:SerializedName("page")
	val page: Int? = null,

	@field:SerializedName("totalDocs")
	val totalDocs: Int? = null
)
