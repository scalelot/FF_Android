package com.festum.festumfield.verstion.firstmodule.viemodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.verstion.firstmodule.screens.BaseViewModel
import com.festum.festumfield.verstion.firstmodule.sources.ApiBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.CreateGroupBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.FindFriendsBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.FriendListBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.SendRequestBody
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.FestumFieldApi
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.GroupListItems
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ProfilePictureResponse
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ProfileResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FriendsListViewModel @Inject constructor(
    private val api: FestumFieldApi
) : BaseViewModel() {

    var friendsListData = MutableLiveData<ArrayList<FriendsListItems>?>()
    var groupsListData = MutableLiveData<ArrayList<GroupListItems>?>()
    var profileData = MutableLiveData<ProfileResponse?>()
    var findFriendsData = MutableLiveData<ArrayList<ProfileResponse?>?>()
    var sendRequestData = MutableLiveData<ApiBody?>()
    var groupProfilePictureData = MutableLiveData<ProfilePictureResponse?>()
    var createGroupData = MutableLiveData<GroupListItems?>()
    var addMembersData = MutableLiveData<GroupListItems?>()
    var removeMembersData = MutableLiveData<GroupListItems?>()

    fun friendsList(friendListBody: FriendListBody){
        api.getFriendsList(friendListBody).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                friendsListData.value = resp.Data
            }, {
                friendsListData.value = null
                Log.e("TAG", "friendsList:--- " + it.message )
            })
    }

    fun groupsList(friendListBody: FriendListBody){
        api.getGroupsList(friendListBody).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                groupsListData.value = resp.Data
                Log.e("TAG", "groupsListData:--- " + resp.Data.toString() )
            }, {
                groupsListData.value = null
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

    fun setGroupProfilePicture(file: File?) {

        if (file != null) {

            AndroidNetworking.upload(Constans.set_group_pic).addMultipartFile("file", file)
                .addHeaders("Authorization", AppPreferencesDelegates.get().token)
                .setPriority(Priority.HIGH).build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {

                        val profileImageData = response.getJSONObject("Data")
                        val profilePictureResponse = ProfilePictureResponse(
                            s3Url = profileImageData.optString("s3_url"),
                            key = profileImageData.optString("Key")
                        )

                        groupProfilePictureData.value = profilePictureResponse

                    }

                    override fun onError(anError: ANError) {

                        groupProfilePictureData.value = null

                    }

                })

        }

    }

    fun createGroup(groupBody: CreateGroupBody){
        api.createGroup(groupBody).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                Log.e("TAG", "createGroup: " + resp.Data )
                createGroupData.value = resp.Data
            }, {
                Log.e("TAG", "createGroup: " + it.localizedMessage )
                createGroupData.value = null
            })
    }

    fun addMembers(groupBody: CreateGroupBody){
        api.addMembersInGroup(groupBody).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                Log.e("TAG", "addMembersData: " + resp.Data )
                addMembersData.value = resp.Data
            }, {
                Log.e("TAG", "addMembersData: " + it.localizedMessage )
                addMembersData.value = null
            })
    }

    fun removeMembers(groupBody: CreateGroupBody){
        api.removeMembersInGroup(groupBody).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                Log.e("TAG", "addMembersData: " + resp.Data )
                removeMembersData.value = resp.Data
            }, {
                Log.e("TAG", "addMembersData: " + it.localizedMessage )
                removeMembersData.value = null
            })
    }

}