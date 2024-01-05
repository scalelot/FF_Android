package com.festum.festumfield.verstion.firstmodule.screens.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.View
import com.bumptech.glide.Glide
import com.festum.festumfield.R
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.Utils.FileUtils
import com.festum.festumfield.databinding.FragmentBusinessInfromationBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseFragment
import com.festum.festumfield.verstion.firstmodule.screens.main.profile.CreateBusinessProfileActivity
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ChatUserResponse
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ProfileResponse
import com.festum.festumfield.verstion.firstmodule.viemodels.ProfileViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendBusinessInformationFragment(var chatUserResponse : ChatUserResponse) : BaseFragment<ProfileViewModel>() {

    private lateinit var binding: FragmentBusinessInfromationBinding

    override fun getContentView(): View {

        binding = FragmentBusinessInfromationBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun initUi() {

        getInformation()

    }

    override fun onResume() {
        super.onResume()

        getInformation()

    }
    @SuppressLint("SetTextI18n")
    override fun setObservers() {

        

    }

    private fun getInformation(){


        binding.idPBLoading.visibility = View.GONE

        if (chatUserResponse.business != null){

            Glide.with(requireContext())
                .load(Constans.Display_Image_URL + chatUserResponse.business?.businessimage)
                .placeholder(R.drawable.ic_user)
                .into(binding.cirBusinessImg)

            binding.businessName.text = chatUserResponse.business?.name
            binding.categoryName.text = chatUserResponse.business?.category
            binding.subcategoryName.text = chatUserResponse.business?.subCategory
            binding.discriptionName.text = chatUserResponse.business?.description
            val long = chatUserResponse.business?.location?.coordinates?.get(0)
            val lat = chatUserResponse.business?.location?.coordinates?.get(1)
            if (lat != null && long != null){
                val latLng = LatLng(lat,long)
                binding.txtBusinessLocation.text = FileUtils.getAddressFromLatLng(context, latLng)
            }else{
                binding.txtBusinessLocation.text = "Address is not found"
            }
            binding.txtBusinessCategory.text = chatUserResponse.business?.interestedCategory
            binding.txtBusinessSubcategory.text = chatUserResponse.business?.interestedSubCategory

            if (chatUserResponse.business?.brochure != null){

                val brochure: String = chatUserResponse.business?.brochure.toString()
                val pdfName = brochure.substring(brochure.lastIndexOf('/') + 1)

                if (pdfName.isEmpty()) {
                    binding.relBrochure.visibility = View.GONE
                } else {
                    binding.businessBrochure.text = pdfName
                }

                binding.relBrochure.setOnClickListener {
                    val uri = Uri.parse(Constans.Display_Image_URL + chatUserResponse.business?.brochure)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }

            } else{

                binding.businessBrochure.text = "No file"

            }

        }

        binding.btnCreateProfile.setOnClickListener {
            startActivity(Intent(requireContext(), CreateBusinessProfileActivity::class.java))
        }

    }

}