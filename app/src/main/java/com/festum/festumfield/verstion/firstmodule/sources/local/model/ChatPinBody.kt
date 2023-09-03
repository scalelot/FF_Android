package com.festum.festumfield.verstion.firstmodule.sources.local.model

import com.google.gson.annotations.SerializedName

data class ChatPinBody(
    @field:SerializedName("userid")
    val userid: String? = null,

    @field:SerializedName("isPinned")
    val isPinned: Boolean? = null
)