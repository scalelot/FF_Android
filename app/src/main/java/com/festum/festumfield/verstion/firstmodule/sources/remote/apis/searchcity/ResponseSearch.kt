package com.festum.festumfield.verstion.firstmodule.sources.remote.apis.searchcity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResponseSearch(

    @SerializedName("AdministrativeArea")
    var administrativeArea: AdministrativeArea? = null,

    @SerializedName("Type")
    var type: String? = null,

    @SerializedName("Version")
    var version: Int? = null,

    @SerializedName("LocalizedName")
    var localizedName: String? = null,

    @SerializedName("Country")
    var country: Country? = null,

    @SerializedName("Rank")
    var rank: Int? = null,

    @SerializedName("Key")
    var key: String? = null

) : Serializable
