package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Product(

    @field:SerializedName("productid")
    val productid: ProductItem? = null

) : Serializable
