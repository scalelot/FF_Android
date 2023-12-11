package com.festum.festumfield.verstion.firstmodule.screens.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.festum.festumfield.R
import com.festum.festumfield.databinding.FragmentCallHistoryBinding
import com.festum.festumfield.databinding.FragmentMapBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseFragment
import com.festum.festumfield.verstion.firstmodule.screens.adapters.CallHistoryAdapter
import com.festum.festumfield.verstion.firstmodule.screens.adapters.FriendsListAdapter
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.CallHistoryItem
import com.festum.festumfield.verstion.firstmodule.viemodels.ChatViewModel
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendsListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CallHistoryFragment : BaseFragment<ChatViewModel>() {

    private lateinit var binding: FragmentCallHistoryBinding
    private var callHistoryAdapter : CallHistoryAdapter? = null

    override fun getContentView(): View {
        binding = FragmentCallHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initUi() {

        viewModel.callHistory()

    }

    override fun setObservers() {

        viewModel.callHistoryData.observe(this) { callHistoryData ->


            callHistoryData?.reverse()

            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {

                callHistoryAdapter = CallHistoryAdapter(requireActivity(), callHistoryData as ArrayList<CallHistoryItem?>)
                binding.recycleCall.adapter = callHistoryAdapter

            }

            if (callHistoryData == null){

            }

        }

    }


}