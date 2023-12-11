package com.festum.festumfield.verstion.firstmodule.viemodels

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.festum.festumfield.MyApplication
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.verstion.firstmodule.FestumApplicationClass
import com.festum.festumfield.verstion.firstmodule.screens.BaseViewModel
import com.festum.festumfield.verstion.firstmodule.sources.ApiBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.CallEndBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.CallHistoryBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.CallStartBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.ChatListBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.FriendListBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.GetFriendProduct
import com.festum.festumfield.verstion.firstmodule.sources.local.model.MessageStatusBody
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.FestumFieldApi
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val api: FestumFieldApi
) : BaseViewModel() {

    var chatData = MutableLiveData<ArrayList<DocsItem>?>()
    var sendData = MutableLiveData<SendMessageResponse?>()
    var productData = MutableLiveData<ProductResponse?>()
    var callStartData = MutableLiveData<CallResponse?>()
    var callEndData = MutableLiveData<CallResponse?>()
    var callHistoryData = MutableLiveData<ArrayList<CallHistoryItem>?>()
    var messageDeliverData = MutableLiveData<ApiBody?>()
    var messageSeenData = MutableLiveData<ApiBody?>()

    fun getChatMessageHistory(receiverId: String, page: Int, limit: Int) {

        val chatBody = ChatListBody(limit, receiverId, page)
        api.chatList(chatBody).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                chatData.value = resp.Data?.docs as ArrayList<DocsItem>?
            }, {
                chatData.value = null
            })
    }

    fun sendMessage(file: File?, receiverId: String, message: String?, product: String?) {

        if (file != null) {

            try {
                AndroidNetworking.upload(Constans.set_chat_message).addMultipartFile("file", file)
                    .addMultipartParameter("message",message)
                    .addMultipartParameter("to", receiverId)
                    .addHeaders("Authorization", AppPreferencesDelegates.get().token)
                    .setPriority(Priority.HIGH).build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            val messageData = response.getJSONObject("Data")

                            val contentList = messageData.getJSONObject("content")

                            val messageList = contentList.getJSONObject("text")
                            val mediaList = contentList.getJSONObject("media")
                            val product = contentList.getJSONObject("product")

                            val content = SendMessageContent(
                                text = SendText(
                                    messageList.optString("message")
                                ),
                                media = SendMedia(
                                    path = mediaList.optString("path"),
                                    mime = mediaList.optString("mime"),
                                    name = mediaList.optString("name"),
                                    type = mediaList.optString("type")
                                ),
                                product = SendProduct(
                                    productid =  product.optString("productid")
                                )
                            )

                            val newItem = SendMessageResponse(
                                createdAt = messageData.getString("createdAt"),
                                v = messageData.getInt("__v"),
                                context = messageData.getString("context"),
                                from = messageData.getString("from"),
                                mainId = messageData.getString("_id"),
                                to =  messageData.getString("to"),
                                id = messageData.getString("_id"),
                                contentType = messageData.getString("contentType"),
                                content = content,
                                timestamp = messageData.getLong("timestamp"),
                                status = messageData.getString("status"),
                                updatedAt = messageData.getString("updatedAt")

                            )

                            Log.e("TAG", "onResponse:-- " + newItem )

                            sendData.value = newItem
                        }

                        override fun onError(error: ANError) {
                            Toast.makeText(
                                FestumApplicationClass.appInstance,
                                "Not Upload Image",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("SendChatImageError=>", error.toString())
                        }
                    })
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {

            val to = receiverId.toRequestBody("text/plain".toMediaTypeOrNull())
            val sendMessage = message?.toRequestBody("text/plain".toMediaTypeOrNull())
            val productId = product?.toRequestBody("text/plain".toMediaTypeOrNull())

            api.sendMessage(null,to,sendMessage,productId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ resp ->
                    sendData.value = resp.Data
                }, {
                    sendData.value = null
                })
        }

    }

    fun getProduct(productId: String){
        api.getProductById(productId).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                productData.value = resp.Data
            }, {
                productData.value = null
            })
    }

    fun getMessageDeliver(messageId: String){

        val messageItem = MessageStatusBody(messageid = messageId)

        api.messageDelivered(messageItem).enqueue(object : Callback<ApiBody> {
            override fun onResponse(call: Call<ApiBody>, response: Response<ApiBody>) {

                if (response.isSuccessful) {
                    messageDeliverData.value = response.body()
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<ApiBody>() {}.type
                    val errorResponse: ApiBody? =
                        gson.fromJson(response.errorBody()?.charStream(), type)
                    messageDeliverData.value = errorResponse
                }
            }

            override fun onFailure(call: Call<ApiBody>, t: Throwable) {
                messageDeliverData.value = null
            }

        })
    }

    fun getMessageSeen(messageId: String){

        val messageItem = MessageStatusBody(messageid = messageId)

        api.messageSeen(messageItem).enqueue(object : Callback<ApiBody> {
            override fun onResponse(call: Call<ApiBody>, response: Response<ApiBody>) {

                if (response.isSuccessful) {
                    messageSeenData.value = response.body()
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<ApiBody>() {}.type
                    val errorResponse: ApiBody? =
                        gson.fromJson(response.errorBody()?.charStream(), type)
                    messageSeenData.value = errorResponse
                }
            }

            override fun onFailure(call: Call<ApiBody>, t: Throwable) {
                messageSeenData.value = null
            }

        })
    }

    fun callHistory(){

        val callHistoryBody = CallHistoryBody(limit = Int.MAX_VALUE, page = 1)
        api.getCallHistory(callHistoryBody).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                callHistoryData.value = resp.Data?.docs as ArrayList<CallHistoryItem>
            }, {
                callHistoryData.value = null
            })

    }

    fun callStart(from : String?, to : String?, isVideoCall : Boolean, isGroupCall : Boolean, status : String){

        val callStartBody = CallStartBody(from = from, to = to, isVideoCall = isVideoCall, isGroupCall = isGroupCall, status = status)

        api.callStart(callStartBody).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                callStartData.value = resp.Data
            }, {
                callStartData.value = null
            })

    }

    fun callEnd(callId : String?){

        val callEndBody = CallEndBody(callId = callId)

        api.callEnd(callEndBody).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                callEndData.value = resp.Data
            }, {
                callEndData.value = null
            })

    }

}

