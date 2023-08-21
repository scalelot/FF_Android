package com.festum.festumfield.verstion.firstmodule.screens.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.festum.festumfield.R
import com.festum.festumfield.databinding.FragmentBusinessInfromationBinding
import com.festum.festumfield.databinding.FragmentReelsBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseFragment
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ProfileResponse
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendsListViewModel
import com.festum.festumfield.verstion.firstmodule.viemodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReelsFragment(profileData: ProfileResponse) : BaseFragment<ProfileViewModel>() {

    private lateinit var binding: FragmentReelsBinding

    override fun getContentView(): View {

        binding = FragmentReelsBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun initUi() {

    }

    override fun setObservers() {

    }

}