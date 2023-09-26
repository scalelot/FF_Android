package com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces

import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import java.util.ArrayList

interface GroupInterface {

    fun onAddMemberClick(items: FriendsListItems)

    fun onRemoveMemberClick(items: FriendsListItems, memberList : ArrayList<FriendsListItems>)

}