package com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces

import java.io.File

interface SendImageInterface {

    fun onSendImage(file: File, message: String)

}