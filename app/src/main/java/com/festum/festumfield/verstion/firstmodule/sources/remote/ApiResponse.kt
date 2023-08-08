package com.app.easyday.app.sources

import java.io.Serializable

class ApiResponse<T>(
    var IsSuccess: Boolean = false,
    var Message: String? = null,
    var Status: String? = null,
    var Data: T? = null

) : Serializable {
    override fun toString(): String {
        return "ApiResponse(IsSuccess=$IsSuccess, Message=$Message, Status=$Status, Data=$Data)"
    }
}