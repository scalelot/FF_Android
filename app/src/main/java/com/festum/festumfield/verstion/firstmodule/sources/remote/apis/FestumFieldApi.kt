package com.festum.festumfield.verstion.firstmodule.sources.remote.apis

import com.app.easyday.app.sources.ApiResponse
import com.festum.festumfield.verstion.firstmodule.sources.ApiBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.ChatListBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.ChatPinBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.CreateBusinessProfileModel
import com.festum.festumfield.verstion.firstmodule.sources.local.model.CreateGroupBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.CreateProfileModel
import com.festum.festumfield.verstion.firstmodule.sources.local.model.FindFriendsBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.FriendListBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.GetFriendProduct
import com.festum.festumfield.verstion.firstmodule.sources.local.model.SendMessage
import com.festum.festumfield.verstion.firstmodule.sources.local.model.SendRequestBody
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
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

    @POST("profile/setprofile")
    fun createPersonProfile(
        @Body body: CreateProfileModel
    ) : Call<ApiBody>

    @POST("business/setbusiness")
    fun createBusinessProfile(
        @Body body: CreateBusinessProfileModel
    ) : Call<ApiBody>

    @POST("friends/findfriends")
    fun findFriendByLocation(
        @Body body: FindFriendsBody
    ) : Observable<ApiResponse<ArrayList<ProfileResponse>>>

    @POST("friends/sendfriendrequest")
    fun sendFriendRequest(
        @Body body: SendRequestBody
    ) : Call<ApiBody>

    @POST("pinned")
    fun sendPin(
        @Body body: ChatPinBody
    ) : Call<ApiBody>

    @POST("group")
    fun createGroup(
        @Body createGroupBody : CreateGroupBody
    ) : Observable<ApiResponse<GroupListItems>>

    @POST("group/addmembers")
    fun addMembersInGroup(
        @Body createGroupBody : CreateGroupBody
    ) : Observable<ApiResponse<GroupListItems>>

    @POST("group/removemembers")
    fun removeMembersInGroup(
        @Body createGroupBody : CreateGroupBody
    ) : Observable<ApiResponse<GroupListItems>>

}