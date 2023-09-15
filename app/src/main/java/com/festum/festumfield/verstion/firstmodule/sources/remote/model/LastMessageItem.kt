package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LastMessageItem(

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("__v")
    val v: Int? = null,

    @field:SerializedName("context")
    val context: Any? = null,

    @field:SerializedName("from")
    val from: String? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("to")
    val to: String? = null,

    @field:SerializedName("contentType")
    val contentType: String? = null,

    @field:SerializedName("content")
    val content: Content? = null,

    @field:SerializedName("timestamp")
    val timestamp: Long? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null

) : Serializable