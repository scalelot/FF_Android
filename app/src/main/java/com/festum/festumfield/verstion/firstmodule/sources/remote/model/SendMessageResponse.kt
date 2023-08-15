package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.festum.festumfield.verstion.firstmodule.sources.local.model.ListItem
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SendMessageResponse(

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("__v")
    val v: Int? = null,

    @field:SerializedName("context")
    val context: Any? = null,

    @field:SerializedName("from")
    val from: String? = null,

    @field:SerializedName("_id")
    val mainId: String? = null,

    @field:SerializedName("to")
    val to: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("contentType")
    val contentType: String? = null,

    @field:SerializedName("content")
    val content: SendMessageContent? = null,

    @field:SerializedName("timestamp")
    val timestamp: Long? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null
) : Serializable, ListItem(null) {
    override fun toString(): String {
        return "SendMessageResponse(createdAt=$createdAt, v=$v, context=$context, from=$from, mainId=$mainId, to=$to, id=$id, contentType=$contentType, content=$content, timestamp=$timestamp, status=$status, updatedAt=$updatedAt)"
    }
}
