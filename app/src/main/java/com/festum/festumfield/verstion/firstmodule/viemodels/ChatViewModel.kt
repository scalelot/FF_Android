package com.festum.festumfield.verstion.firstmodule.viemodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.easyday.screens.base.BaseViewModel
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.FestumFieldApi
import com.festum.festumfield.verstion.firstmodule.sources.local.model.ChatListBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.SendMessage
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ChatMessageResponse
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.SendMessageResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val api: FestumFieldApi
) : BaseViewModel() {

    var chatData = MutableLiveData<ChatMessageResponse?>()
    var sendData = MutableLiveData<SendMessageResponse?>()

    fun getChatMessageHistory(receiverId: String, page: Int, limit: Int) {

        val chatBody = ChatListBody(limit,receiverId,page)
        api.chatList(chatBody).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                chatData.value = resp.Data
                Log.e("TAG", "getChatMessageHistory: ${resp.Data}")
            }, {
                Log.e("TAG", "Throwable: " + it.message )
                chatData.value = null
            })
    }

    fun sendMessage(receiverId: String, message : String) {

        val sendMessage = SendMessage(to = receiverId, message = message)
        api.sendMessage(sendMessage).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                sendData.value = resp.Data
                Log.e("TAG", "getChatMessageHistory: ${resp.Data}")
            }, {
                Log.e("TAG", "Throwable: " + it.message )
                sendData.value = null
            })
    }

}