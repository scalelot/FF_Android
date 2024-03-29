package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Location(

    @field:SerializedName("coordinates")
    val coordinates: List<Double?>? = null,

    @field:SerializedName("type")
    val type: String? = null

) : Serializable
