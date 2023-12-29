package com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces

import com.festum.festumfield.verstion.firstmodule.sources.remote.model.GroupMembersListItems


interface GroupMembersInterface {

    fun onAddMemberClick(items: GroupMembersListItems, b: Boolean, addMembersList: ArrayList<String>)

    fun onRemoveMemberClick(items: GroupMembersListItems, position : Int)

    fun onMembersList(position : Int)

    fun onRemoveMember(items: GroupMembersListItems)

}