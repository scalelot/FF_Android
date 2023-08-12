package com.festum.festumfield.verstion.firstmodule.viemodels

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.app.easyday.screens.base.BaseViewModel
import com.festum.festumfield.MyApplication
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.verstion.firstmodule.sources.local.model.ChatListBody
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.FestumFieldApi
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
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

    fun getChatMessageHistory(receiverId: String, page: Int, limit: Int) {

        val chatBody = ChatListBody(limit, receiverId, page)
        api.chatList(chatBody).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                chatData.value = resp.Data?.docs as ArrayList<DocsItem>?
                Log.e("TAG", "getChatMessageHistory: ${resp.Data}")
            }, {
                Log.e("TAG", "Throwable: " + it.message)
                chatData.value = null
            })
    }

    fun sendMessage(file: File?, receiverId: String, message: String?, product: String?) {

        if (file != null) {
            /*val sendFile = File(file.toString())
            val fileRequestBody = sendFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val sendImage = createFormData("file", sendFile.name, fileRequestBody)

            val to = receiverId.toRequestBody("text/plain".toMediaTypeOrNull())
            api.sendMessage(sendImage,to,null).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ resp ->
                    sendData.value = resp.Data
                    Log.e("TAG", "getChatImageHistory: ${resp.Data}")
                }, {
                    Log.e("TAG", "Throwable: " + it.message)
                    sendData.value = null
                })*/

            try {
                AndroidNetworking.upload(Constans.set_chat_message).addMultipartFile("file", file)
                    .addMultipartParameter("to", receiverId)
                    .addHeaders("Authorization", MyApplication.getAuthToken(MyApplication.context))
                    .setPriority(Priority.HIGH).build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            val messageData = response.getJSONObject("Data")

                            val contentList = messageData.getJSONObject("content")

                            val messageList = contentList.getJSONObject("text")
                            val mediaList = contentList.getJSONObject("media")
                            val product = contentList.getJSONObject("product")

                            val content = Content(
                                text = Text(
                                    messageList.optString("message")
                                ),
                                media = Media(
                                    path = mediaList.optString("path"),
                                    mime = mediaList.optString("mime"),
                                    name = mediaList.optString("name"),
                                    type = mediaList.optString("type")
                                ),
                                product = Product(
                                    ProductItem(id = product.optString("productid"))
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

                            sendData.value = newItem
                        }

                        override fun onError(error: ANError) {
                            Toast.makeText(
                                MyApplication.context,
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
                    Log.e("TAG", "getChatMessageHistory: ${resp.Data}")
                }, {
                    Log.e("TAG", "Throwable: " + it.message)
                    sendData.value = null
                })
        }

    }

    fun getProduct(productId: String){
        api.getProductById(productId).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                productData.value = resp.Data
                Log.e("TAG", "getProductById: ${resp.Data}")
            }, {
                Log.e("TAG", "Throwable: " + it.message)
                productData.value = null
            })
    }

}

