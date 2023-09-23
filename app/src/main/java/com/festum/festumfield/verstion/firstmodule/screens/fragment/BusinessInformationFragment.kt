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
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ProfileResponse
import com.festum.festumfield.verstion.firstmodule.viemodels.ProfileViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BusinessInformationFragment(var profileData: ProfileResponse) : BaseFragment<ProfileViewModel>() {

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

        viewModel.profileBusinessData.observe(this) { profileBusinessData ->

            if (profileBusinessData != null){

                Glide.with(requireContext())
                    .load(Constans.Display_Image_URL + profileBusinessData.businessimage)
                    .placeholder(R.drawable.ic_user)
                    .into(binding.cirBusinessImg)

                binding.businessName.text = profileBusinessData.name
                binding.categoryName.text = profileBusinessData.category
                binding.subcategoryName.text = profileBusinessData.subCategory
                binding.discriptionName.text = profileBusinessData.description
                val long = profileBusinessData.location?.coordinates?.get(0)
                val lat = profileBusinessData.location?.coordinates?.get(1)
                if (lat != null && long != null){
                    val latLng = LatLng(lat,long)
                    binding.txtBusinessLocation.text = FileUtils.getAddressFromLatLng(context, latLng)
                }else{
                    binding.txtBusinessLocation.text = "Address is not found"
                }
                binding.txtBusinessCategory.text = profileBusinessData.interestedCategory
                binding.txtBusinessSubcategory.text = profileBusinessData.interestedSubCategory

                if (profileBusinessData.brochure != null){

                    val brochure: String = profileBusinessData.brochure
                    val pdfName = brochure.substring(brochure.lastIndexOf('/') + 1)
                    binding.businessBrochure.text = pdfName

                    binding.relBrochure.setOnClickListener {
                        val uri = Uri.parse(Constans.Display_Image_URL + profileBusinessData.brochure)
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                    }

                } else{

                    binding.businessBrochure.text = "No file"

                }

            }
            binding.idPBLoading.visibility = View.GONE

        }

    }

    private fun getInformation(){
        if (profileData.isBusinessProfileCreated == false){

            binding.dialog.visibility = View.VISIBLE
            binding.createProfile.visibility = View.GONE
            binding.idPBLoading.visibility = View.GONE

        } else {

            viewModel.getBusinessProfile()

            binding.dialog.visibility = View.GONE
            binding.createProfile.visibility = View.VISIBLE

        }

        binding.btnCreateProfile.setOnClickListener {
            startActivity(Intent(requireContext(), CreateBusinessProfileActivity::class.java))
        }

    }

}