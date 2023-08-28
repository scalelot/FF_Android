package com.festum.festumfield.verstion.firstmodule.viemodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.festum.festumfield.verstion.firstmodule.screens.BaseViewModel
import com.festum.festumfield.verstion.firstmodule.sources.local.model.FindFriendsBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.FriendListBody
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.FestumFieldApi
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ProfileResponse
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun friendsList(friendListBody: FriendListBody){
        api.getFriendsListProduct(friendListBody).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                friendsListData.value = resp.Data?.docs as ArrayList<FriendsListItems>?
            }, {
                friendsListData.value = null
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

}