package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FriendsProducts(

    @field:SerializedName("subCategory")
    val subCategory: String? = null,

    @field:SerializedName("images")
    val images: ArrayList<String?>? = null,

    @field:SerializedName("updatedBy")
    val updatedBy: String? = null,

    @field:SerializedName("itemCode")
    val itemCode: String? = null,

    @field:SerializedName("businessid")
    val businessid: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("userid")
    val userid: String? = null,

    @field:SerializedName("offer")
    val offer: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("createdBy")
    val createdBy: String? = null,

    @field:SerializedName("price")
    val price: Int? = null,

    @field:SerializedName("__v")
    val v: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("_id")
    val mainId: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("category")
    val category: String? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null
)  : Serializable