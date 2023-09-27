package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Media(

    @field:SerializedName("path")
    val path: String? = null,

    @field:SerializedName("mime")
    val mime: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("type")
    val type: String? = null
) : Serializable
