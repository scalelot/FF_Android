package com.festum.festumfield.verstion.firstmodule.screens.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import com.festum.festumfield.Adapter.TagAdapter
import com.festum.festumfield.Utils.FileUtils
import com.festum.festumfield.databinding.FragmentPersonalInformationBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseFragment
import com.festum.festumfield.verstion.firstmodule.screens.main.profile.CreatePersonProfileActivity
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ChatUserResponse
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ProfileResponse
import com.festum.festumfield.verstion.firstmodule.viemodels.ProfileViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class FriendInformationFragment(private var chatUserResponse: ChatUserResponse) : BaseFragment<ProfileViewModel>() {

    private lateinit var binding: FragmentPersonalInformationBinding

    override fun getContentView(): View {
        binding = FragmentPersonalInformationBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun initUi() {

        getInformation()

    }

    override fun onResume() {
        super.onResume()

        getInformation()

    }

    override fun setObservers() {

    }

    private fun getInformation(){

        /*if (chatUserResponse.fullName.isNullOrEmpty() || chatUserResponse.contactNo.isNullOrEmpty()){

            binding.dialog.visibility = View.VISIBLE
            binding.profileContainer.visibility = View.GONE
            binding.idPBLoading.visibility = View.GONE

        } else {



        }*/

        binding.relativeLocation.visibility = View.GONE
        binding.relativeIntersted.visibility = View.GONE

        if (chatUserResponse.aboutUs.isNullOrEmpty()){ binding.relativeAbove.visibility = View.GONE}
        if (chatUserResponse.hobbies.isNullOrEmpty()){ binding.relativeHobbies.visibility = View.GONE}
        /*if (chatUserResponse.location?.coordinates.isNullOrEmpty()){ binding.relativeLocation.visibility = View.GONE}*/
        /*if (chatUserResponse.interestedin.isNullOrEmpty()){ binding.relativeIntersted.visibility = View.GONE}*/

        binding.txtAbout.text = chatUserResponse.aboutUs
        binding.txtBirth.text = chatUserResponse.dob
        binding.txtGender.text = chatUserResponse.gender
        binding.txtNumber.text = chatUserResponse.contactNo
        binding.txtEmail.text = chatUserResponse.emailId
        binding.textRage.text = chatUserResponse.areaRange.toString()
        binding.textInteresIn.text = chatUserResponse.interestedin
        binding.faceId.visibility = View.GONE
        /*binding.textAge.text = "${chatUserResponse.targetAudienceAgeMin} " +" - "+"${chatUserResponse.targetAudienceAgeMax}"
        val long = chatUserResponse.location?.coordinates?.get(0)
        val lat = chatUserResponse.location?.coordinates?.get(1)
        if (lat != null && long != null){
            val latLng = LatLng(lat,long)
            binding.textLocation.text = FileUtils.getAddressFromLatLng(context, latLng)
        }else{
            binding.textLocation.text = "Address is not found"
        }*/

        val tagAdapter = TagAdapter(requireContext(), chatUserResponse.hobbies as ArrayList<String>)
        binding.recyTag.adapter = tagAdapter

        binding.dialog.visibility = View.GONE
        binding.profileContainer.visibility = View.VISIBLE
        binding.idPBLoading.visibility = View.GONE

    }

}