package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MembersItem (

    @field:SerializedName("startedAt")
    val startedAt: Long? = null,

    @field:SerializedName("endAt")
    val endAt: Long? = null,

    @field:SerializedName("memberid")
    val memberid: String? = null,

    @field:SerializedName("status")
    val status: String? = null

) : Serializable