package com.festum.festumfield.verstion.firstmodule.screens.fragment

import android.os.Handler
import android.view.View
import com.festum.festumfield.R
import com.festum.festumfield.databinding.FragmentMapBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseFragment
import com.festum.festumfield.verstion.firstmodule.sources.local.model.FindFriendsBody
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendsListViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MapFragment : BaseFragment<FriendsListViewModel>(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding
    private var googleMap: GoogleMap? = null
    override fun getContentView(): View {
        binding = FragmentMapBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initUi() {

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        /* Get Profile */
        viewModel.getProfile()

    }

    override fun setObservers() {

        viewModel.profileData.observe(this) { profileData ->

            if (profileData?.location?.coordinates.isNullOrEmpty()) {

            } else {

                val findFriendsList = FindFriendsBody(
                    search = "",
                    longitude = profileData?.location?.coordinates?.get(0),
                    latitude = profileData?.location?.coordinates?.get(1)
                )

                viewModel.findFriendsByLocation(findFriendsList)

            }

        }

        viewModel.findFriendsData.observe(this) { findFriendsData ->

            findFriendsData?.forEach {

                val longitude = it?.location?.coordinates?.get(0)
                val latitude = it?.location?.coordinates?.get(1)

                Handler().postDelayed({

                    if (latitude != null && longitude != null) {
                        val userLocation = LatLng(latitude, longitude)
                        googleMap?.addMarker(MarkerOptions().position(userLocation))
                        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12f))
                    }

                }, 700)

            }

        }

    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }

}
