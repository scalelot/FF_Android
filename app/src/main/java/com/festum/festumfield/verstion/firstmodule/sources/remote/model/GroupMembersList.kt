package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GroupMembersList(
    @field:SerializedName("_id")
    val id : String? = null,
) : Serializable
