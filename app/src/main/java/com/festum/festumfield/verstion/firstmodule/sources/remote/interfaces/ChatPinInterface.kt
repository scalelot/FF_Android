package com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces

import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems

interface ChatPinInterface {

    fun checkItemPin(friendItem : FriendsListItems)
    fun setPin(friendItem : FriendsListItems)


}