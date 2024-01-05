package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Business(

    @field:SerializedName("subCategory")
    val subCategory: String? = null,

    @field:SerializedName("interestedSubCategory")
    val interestedSubCategory: String? = null,

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("brochure")
    val brochure: String? = null,

    @field:SerializedName("interestedCategory")
    val interestedCategory: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("location")
    val location: Location? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("businessimage")
    val businessimage: String? = null,

    @field:SerializedName("category")
    val category: String? = null

) : Serializable
