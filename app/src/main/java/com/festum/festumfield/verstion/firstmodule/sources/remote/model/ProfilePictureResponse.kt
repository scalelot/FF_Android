package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProfilePictureResponse(

	@field:SerializedName("s3_url")
	val s3Url: String? = null,

	@field:SerializedName("Key")
	val key: String? = null

) : Serializable
