package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Content(

    @field:SerializedName("product")
    val product: Product? = null,

    @field:SerializedName("text")
    val text: Text? = null,

    @field:SerializedName("media")
    val media: Media? = null
) : Serializable

