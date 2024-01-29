package com.festum.festumfield.verstion.firstmodule.sources.local.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SendOtpBody(

    @field:SerializedName("countryCode")
    val countryCode: String? = null,

    @field:SerializedName("contactNo")
    val contactNo: String? = null,

    @field:SerializedName("fcmtoken")
    val fcmtoken: String? = null

) : Serializable