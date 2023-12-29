package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.security.Permissions

data class GroupMembersList(
    @field:SerializedName("_id")
    val id : String? = null,

    @field:SerializedName("permissions")
    val permissions: Permissions? = null
) : Serializable
