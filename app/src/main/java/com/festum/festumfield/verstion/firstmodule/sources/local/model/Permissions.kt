package com.festum.festumfield.verstion.firstmodule.sources.local.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Permissions(

    @field:SerializedName("videocall")
    val videocall: Boolean? = null,

    @field:SerializedName("gender")
    val gender: Boolean? = null,

    @field:SerializedName("contactnumber")
    val contactnumber: Boolean? = null,

    @field:SerializedName("dob")
    val dob: Boolean? = null,

    @field:SerializedName("socialmedia")
    val socialmedia: Boolean? = null,

    @field:SerializedName("audiocall")
    val audiocall: Boolean? = null,

    @field:SerializedName("fullname")
    val fullname: Boolean? = null,

    @field:SerializedName("email")
    val email: Boolean? = null

) : Serializable
