package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SocialMediaLinksItem(

    @field:SerializedName("link")
    val link: String? = null,

    @field:SerializedName("platform")
    val platform: String? = null

) : Serializable