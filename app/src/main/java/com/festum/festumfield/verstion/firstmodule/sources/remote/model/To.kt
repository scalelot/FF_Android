package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName

data class To(

    @field:SerializedName("contact_no")
    val contactNo: String? = null,

    @field:SerializedName("fullName")
    val fullName: String? = null,

    @field:SerializedName("emailId")
    val emailId: String? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("profileimage")
    val profileimage: String? = null

)
