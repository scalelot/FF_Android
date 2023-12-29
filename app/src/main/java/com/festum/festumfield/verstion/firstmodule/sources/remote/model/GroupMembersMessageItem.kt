package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GroupMembersMessageItem(

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("timestamp")
    val timestamp: Long? = null,


) : Serializable