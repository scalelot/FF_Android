package com.festum.festumfield.verstion.firstmodule.sources.remote.apis

import com.app.easyday.app.sources.ApiResponse
import com.festum.festumfield.verstion.firstmodule.sources.local.model.ChatListBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.SendMessage
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ChatMessageResponse
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.SendMessageResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.POST
import rx.Observable

interface FestumFieldApi {

    @POST("chats")
    fun chatList(
       @Body body: ChatListBody
    ): Observable<ApiResponse<ChatMessageResponse>>

    @POST("chats/send")
    fun sendMessage(
        @Body body: SendMessage
    ):Observable<ApiResponse<SendMessageResponse>>

}