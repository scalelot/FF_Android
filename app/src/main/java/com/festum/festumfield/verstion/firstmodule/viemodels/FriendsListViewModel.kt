package com.festum.festumfield.verstion.firstmodule.viemodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.festum.festumfield.verstion.firstmodule.screens.BaseViewModel
import com.festum.festumfield.verstion.firstmodule.sources.ApiBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.FindFriendsBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.FriendListBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.SendRequestBody
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.FestumFieldApi
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ProfileResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class FriendsListViewModel @Inject constructor(
    private val api: FestumFieldApi
) : BaseViewModel() {

    var friendsListData = MutableLiveData<ArrayList<FriendsListItems>?>()
    var profileData = MutableLiveData<ProfileResponse?>()
    var findFriendsData = MutableLiveData<ArrayList<ProfileResponse?>?>()
    var sendRequestData = MutableLiveData<ApiBody?>()

    fun friendsList(friendListBody: FriendListBody){
        api.getFriendsListProduct(friendListBody).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                friendsListData.value = resp.Data?.docs as ArrayList<FriendsListItems>?
            }, {
                friendsListData.value = null
                Log.e("TAG", "friendsList:--- " + it.message )
            })
    }

    fun getProfile(){
        api.getProfile().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                profileData.value = resp.Data
            }, {
                profileData.value = null
            })
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun findFriendsByLocation(findFriendsBody: FindFriendsBody?) {

        if (findFriendsBody != null) {
            api.findFriendByLocation(findFriendsBody).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ resp ->
                    findFriendsData.value = resp.Data as ArrayList<ProfileResponse?>
                }, {
                    findFriendsData.value = null
                })

        }

    }

    fun sendFriendRequest(sendFriendRequestBody : SendRequestBody?) {

        if (sendFriendRequestBody != null) {
            api.sendFriendRequest(sendFriendRequestBody).enqueue(object : Callback<ApiBody> {
                override fun onResponse(call: Call<ApiBody>, response: Response<ApiBody>) {

                    if (response.isSuccessful){
                        sendRequestData.value = response.body()
                    }else{
                        val gson = Gson()
                        val type = object : TypeToken<ApiBody>() {}.type
                        val errorResponse: ApiBody? = gson.fromJson(response.errorBody()?.charStream(), type)
                        sendRequestData.value = errorResponse
                    }
                }

                override fun onFailure(call: Call<ApiBody>, t: Throwable) {
                    sendRequestData.value = null
                }

            })

        }

    }

}