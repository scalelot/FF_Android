package com.festum.festumfield.verstion.firstmodule.screens.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.festum.festumfield.Fragment.CallsFragment
import com.festum.festumfield.Fragment.ContactFragment
import com.festum.festumfield.Fragment.MapsFragment
import com.festum.festumfield.verstion.firstmodule.screens.fragment.FriendsListFragment

class HomeItemsPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> FriendsListFragment(null,"")
            1 -> MapsFragment()
            2 -> CallsFragment()
            3 ->  ContactFragment()
            else -> FriendsListFragment(null,"")
        }
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence {
        return ""
    }

}