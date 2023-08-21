package com.festum.festumfield.verstion.firstmodule.viemodels

import androidx.lifecycle.MutableLiveData
import com.festum.festumfield.verstion.firstmodule.screens.BaseViewModel
import com.festum.festumfield.verstion.firstmodule.sources.local.model.FriendListBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.GetFriendProduct
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.FestumFieldApi
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.DocsItem
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListResponse
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsProducts
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class FriendsListViewModel @Inject constructor(
    private val api: FestumFieldApi
) : BaseViewModel() {

    var friendsListData = MutableLiveData<ArrayList<FriendsListItems>?>()

    fun friendsList(friendListBody: FriendListBody){
        api.getFriendsListProduct(friendListBody).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                friendsListData.value = resp.Data?.docs as ArrayList<FriendsListItems>?
            }, {
                friendsListData.value = null
            })
    }

}