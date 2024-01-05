package com.festum.festumfield.verstion.firstmodule.sources.local.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GroupPermissionBody(

	@field:SerializedName("permissions")
	val permissions: Permissions? = null,

	@field:SerializedName("groupid")
	val groupid: String? = null

) : Serializable

