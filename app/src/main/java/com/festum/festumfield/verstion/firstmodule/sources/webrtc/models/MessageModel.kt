package com.festum.festumfield.verstion.firstmodule.sources.webrtc.models

data class MessageModel(
     val type: String,
     val name: String? = null,
     val target: String? = null,
     val data:Any?=null
)
