package com.festum.festumfield.verstion.firstmodule.sources

import java.io.Serializable

class ApiResponse<T>(
    var success: Boolean = false,
    var message: String? = null,
    var data: T? = null

) : Serializable {
    override fun toString(): String {
        return "ApiResponse(success=$success, message=$message, data=$data)"
    }
}