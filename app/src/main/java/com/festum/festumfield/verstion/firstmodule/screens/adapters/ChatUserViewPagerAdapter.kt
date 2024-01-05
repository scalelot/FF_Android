package com.festum.festumfield.verstion.firstmodule.screens.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.festum.festumfield.verstion.firstmodule.screens.fragment.FriendBusinessInformationFragment
import com.festum.festumfield.verstion.firstmodule.screens.fragment.FriendInformationFragment
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ChatUserResponse

class ChatUserViewPagerAdapter(var profileData: ChatUserResponse, fm: FragmentManager): FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> FriendInformationFragment(profileData)
            1 -> FriendBusinessInformationFragment(profileData)
            else -> FriendInformationFragment(profileData)
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return ""
    }

}