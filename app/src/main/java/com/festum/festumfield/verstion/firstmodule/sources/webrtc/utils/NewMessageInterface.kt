package com.festum.festumfield.verstion.firstmodule.sources.webrtc.utils

import com.festum.festumfield.verstion.firstmodule.sources.webrtc.models.MessageModel


interface NewMessageInterface {
    fun onNewMessage(message: MessageModel)

}