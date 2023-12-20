package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MembersList(
    @field:SerializedName("_id")
    val membersList: ProfileResponse? = null,

    @field:SerializedName("permissions")
    val permissions: GroupPermissions? = null

) : Serializable
