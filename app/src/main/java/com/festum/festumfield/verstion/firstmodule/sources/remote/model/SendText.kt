package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class SendText(

    @field:SerializedName("message")
    val message: String? = null
)  : Serializable