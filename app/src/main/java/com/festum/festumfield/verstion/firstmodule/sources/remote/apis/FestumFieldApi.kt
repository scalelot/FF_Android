package com.festum.festumfield.verstion.firstmodule.sources.remote.apis

import com.app.easyday.app.sources.ApiResponse
import com.festum.festumfield.verstion.firstmodule.sources.local.model.ChatListBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.FriendListBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.GetFriendProduct
import com.festum.festumfield.verstion.firstmodule.sources.local.model.SendMessage
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import rx.Observable
import java.io.File

interface FestumFieldApi {

    @POST("chats")
    fun chatList(
       @Body body: ChatListBody
    ): Observable<ApiResponse<ChatMessageResponse>>

    @Multipart
    @POST("chats/send")
    fun sendMessage(
        @Part filePart: MultipartBody.Part?,
        @Part("to") to: RequestBody,
        @Part("message") message: RequestBody?,
        @Part("product") product: RequestBody?,
    ):Observable<ApiResponse<SendMessageResponse>>

    @GET("product/single")
    fun getProductById(
        @Query("pid")productId : String
    ):Observable<ApiResponse<ProductResponse>>

    @POST("product/friendsproducts")
    fun getFriendProduct(
        @Body body: GetFriendProduct
    ): Observable<ApiResponse<FriendsProductsResponse>>

    @POST("friends/myfriends")
    fun getFriendsListProduct(
        @Body body: FriendListBody
    ): Observable<ApiResponse<FriendsListResponse>>

    @GET("profile/getprofile")
    fun getProfile(): Observable<ApiResponse<ProfileResponse>>

    @GET("business/getbusiness")
    fun getBusinessProfile(): Observable<ApiResponse<BusinessProfile>>

}