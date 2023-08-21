package com.festum.festumfield.verstion.firstmodule.screens.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import com.festum.festumfield.Activity.BusinessProfileActivity
import com.festum.festumfield.Activity.ProfileActivity
import com.festum.festumfield.Adapter.TagAdapter
import com.festum.festumfield.Utils.FileUtils
import com.festum.festumfield.databinding.FragmentPersonalInformationBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseFragment
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ProfileResponse
import com.festum.festumfield.verstion.firstmodule.viemodels.ProfileViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class PersonalInformationFragment(var profileData: ProfileResponse) : BaseFragment<ProfileViewModel>() {

    private lateinit var binding: FragmentPersonalInformationBinding

    override fun getContentView(): View {
        binding = FragmentPersonalInformationBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun initUi() {

        if (profileData.fullName.isNullOrEmpty()){

            binding.dialog.visibility = View.VISIBLE
            binding.profileContainer.visibility = View.GONE
            binding.idPBLoading.visibility = View.GONE

        } else {

            binding.txtAbout.text = profileData.aboutUs
            binding.txtBirth.text = profileData.dob
            binding.txtGender.text = profileData.gender
            binding.txtNumber.text = profileData.contactNo
            binding.txtEmail.text = profileData.emailId
            binding.textRage.text = profileData.areaRange.toString()
            binding.textInteresIn.text = profileData.interestedin
            binding.textAge.text = "${profileData.targetAudienceAgeMin} " +" - "+"${profileData.targetAudienceAgeMax}"
            val long = profileData.location?.coordinates?.get(0)
            val lat = profileData.location?.coordinates?.get(1)
            if (lat != null && long != null){
                val latLng = LatLng(lat,long)
                binding.textLocation.text = FileUtils.getAddressFromLatLng(context, latLng)
            }else{
                binding.textLocation.text = "Address is not found"
            }

            val tagAdapter = TagAdapter(requireContext(), profileData.hobbies as ArrayList<String>)
            binding.recyTag.adapter = tagAdapter

            binding.dialog.visibility = View.GONE
            binding.profileContainer.visibility = View.VISIBLE
            binding.idPBLoading.visibility = View.GONE

        }

        binding.btnCreateProfile.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }

    }

    override fun setObservers() {

    }

}