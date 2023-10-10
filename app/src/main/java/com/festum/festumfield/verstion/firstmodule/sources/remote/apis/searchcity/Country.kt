package com.festum.festumfield.verstion.firstmodule.sources.remote.apis.searchcity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Country (

    @SerializedName("LocalizedName")
    var localizedName: String? = null,

    @SerializedName("ID")
    var iD: String? = null

) : Serializable