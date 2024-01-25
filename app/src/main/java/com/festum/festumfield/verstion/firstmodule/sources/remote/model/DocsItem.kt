package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.festum.festumfield.verstion.firstmodule.sources.local.model.ListItem
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DocsItem(

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("__v")
    val v: Int? = null,

    @field:SerializedName("context")
    val context: Any? = null,

    @field:SerializedName("from")
    val from: From? = null,

    @field:SerializedName("_id")
    val mainId: String? = null,

    @field:SerializedName("to")
    val to: To? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("contentType")
    val contentType: String? = null,

    @field:SerializedName("content")
    val content: Content? = null,

    @field:SerializedName("timestamp")
    val timestamp: Long? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null,

    @field:SerializedName("callid")
    val callId: CallId? = null

) : Serializable ,ListItem(null)
