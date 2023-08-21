package com.festum.festumfield.verstion.firstmodule.screens.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.festum.festumfield.verstion.firstmodule.screens.fragment.BusinessInformationFragment
import com.festum.festumfield.verstion.firstmodule.screens.fragment.PersonalInformationFragment
import com.festum.festumfield.verstion.firstmodule.screens.fragment.ReelsFragment
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ProfileResponse

class ProfileViewPagerAdapter(var profileData: ProfileResponse, fm: FragmentManager): FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> PersonalInformationFragment(profileData)
            1 -> BusinessInformationFragment(profileData)
            2 -> ReelsFragment(profileData)
            else -> PersonalInformationFragment(profileData)
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        return ""
    }

}