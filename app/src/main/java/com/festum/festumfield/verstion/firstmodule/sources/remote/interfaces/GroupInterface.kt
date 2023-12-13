package com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces

import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems

interface GroupInterface {

    fun onAddMemberClick(items: FriendsListItems, b: Boolean, addMembersList: ArrayList<String>)

    fun onRemoveMemberClick(items: FriendsListItems, position : Int)

    fun onMembersList(position : Int)

    fun onRemoveMember(items: FriendsListItems)

}