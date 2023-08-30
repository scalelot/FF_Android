package com.festum.festumfield.verstion.firstmodule.screens.fragment

import android.animation.FloatEvaluator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.festum.festumfield.R
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.databinding.FragmentMapBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseFragment
import com.festum.festumfield.verstion.firstmodule.sources.local.model.FindFriendsBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.SendRequestBody
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendsListViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.GroundOverlay
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView


@AndroidEntryPoint
class MapFragment : BaseFragment<FriendsListViewModel>(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding
    private var googleMap : GoogleMap? = null
    private var googleMarker : Marker? = null
    private var areaRange : Int? = null
    private val DURATION = 3000

    override fun getContentView(): View {
        binding = FragmentMapBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initUi() {

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        /* Get Profile */
        viewModel.getProfile()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                var addressList: List<Address?>? = null

                if (query?.isNotEmpty() == true){

                    val geocoder = Geocoder(requireActivity())

                    addressList = geocoder.getFromLocationName(query, 1)

                    if (addressList?.size != 0){
                        val address = addressList?.get(0)
                        val latitude = address?.latitude
                        val longitude = address?.longitude

                        if (latitude != null && longitude != null){

                            val latLng = LatLng(latitude, longitude)
                            googleMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                            val cameraPosition = CameraPosition.Builder().target(latLng).zoom(12f).build()
                            googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                        }

                    }

                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

    }

    override fun setObservers() {

        viewModel.profileData.observe(this) { profileData ->

            if (profileData?.location?.coordinates?.isNotEmpty() == true) {

                areaRange = profileData.areaRange

                val longitude = profileData.location.coordinates[0]
                val latitude = profileData.location.coordinates[1]

                /* Find Friend List */
                val findFriendsList = FindFriendsBody(search = "", latitude = latitude, longitude = longitude)

                viewModel.findFriendsByLocation(findFriendsList)

                if (latitude != null && longitude != null){

                    initRadar(LatLng(latitude,longitude))

                }

            } else{}

        }

        viewModel.findFriendsData.observe(this) { findFriendsData ->

            findFriendsData?.forEach { locationItem ->

                val longitude = locationItem?.location?.coordinates?.get(0)
                val latitude = locationItem?.location?.coordinates?.get(1)

                if (latitude != null && longitude != null) {
                    val userLocation = LatLng(latitude, longitude)

                    Glide.with(this)
                        .asBitmap()
                        .load(Constans.Display_Image_URL + locationItem.profileimage)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                val markerIcon = layoutInflater.inflate(R.layout.marker_layout, null)
                                val markerImageView = markerIcon.findViewById<CircleImageView>(R.id.userImage)
                                markerImageView.setImageBitmap(resource)

                                // Add the marker to the Google Map
                                val markerOptions = MarkerOptions()
                                    .position(userLocation)
                                    .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(requireActivity(), markerIcon)))

                                googleMarker = googleMap?.addMarker(markerOptions)
                                googleMarker?.tag = locationItem.channelID
                                googleMarker?.snippet = locationItem.fullName
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                // Handle when the image load is cleared
                            }
                        })
                    /*googleMarker = googleMap?.addMarker(MarkerOptions().position(userLocation))*/
                }

            }

            googleMap?.setOnMarkerClickListener {
                createSendRequest(it.tag.toString().lowercase(), it.snippet.toString())
                false
            }

        }

        viewModel.sendRequestData.observe(this) {

            if (it != null) {
                Snackbar.make(binding.mapviewRoot, it.message.toString(), Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }

    private fun createSendRequest(receiverId: String, userName: String) {

        val viewDialog = LayoutInflater.from(context).inflate(R.layout.map_request_dialog, null)

        val popView = PopupWindow(activity)
        popView.contentView = viewDialog
        popView.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popView.isFocusable = true
        requireActivity().window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val inputEditText = viewDialog.findViewById<TextInputEditText>(R.id.input_edt)
        val userNameText = viewDialog.findViewById<TextView>(R.id.map_username)
        val appCompatButton: AppCompatButton = viewDialog.findViewById(R.id.map_send)
        popView.isOutsideTouchable = false

        userNameText.text = userName

        appCompatButton.setOnClickListener {
            val sendRequestBody =
                SendRequestBody(receiverId = receiverId, message = inputEditText.text.toString())
            viewModel.sendFriendRequest(sendRequestBody)
            popView.dismiss()
        }

        val transparentView =
            View.inflate(context, R.layout.transparent_layout, binding.mapviewRoot)
        val viewMarker = transparentView.findViewById<View>(R.id.view_marker)

        Handler(Looper.getMainLooper()).postDelayed({
            popView.showAsDropDown(viewMarker, -250, -60)
        }, 200)

    }
    private fun createDrawableFromView(context: Context, view: View): Bitmap {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
    private fun initRadar(location: LatLng) {
        val d = GradientDrawable()
        d.shape = GradientDrawable.OVAL
        d.setSize(1000, 1000)
        d.setColor(Color.rgb(90, 200, 210))
        d.setStroke(0, Color.TRANSPARENT)

        val bitmap = Bitmap.createBitmap(d.intrinsicWidth, d.intrinsicHeight, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        d.setBounds(0, 0, canvas.width, canvas.height)
        d.draw(canvas)

        val area: Int = areaRange?.times(1000) ?: 0

        val circle: GroundOverlay? = googleMap?.addGroundOverlay(
            GroundOverlayOptions().position(
                location,
                (2 * area).toFloat()
            ).image(BitmapDescriptorFactory.fromBitmap(bitmap))
        )

        val radiusHolder = PropertyValuesHolder.ofFloat("radius", 0f, area.toFloat())
        val transparencyHolder = PropertyValuesHolder.ofFloat("transparency", 0f, 1f)

        val valueAnimator = ValueAnimator()
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.repeatMode = ValueAnimator.RESTART
        valueAnimator.setValues(radiusHolder, transparencyHolder)
        valueAnimator.duration = DURATION.toLong()
        valueAnimator.setEvaluator(FloatEvaluator())
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.addUpdateListener(AnimatorUpdateListener { animation ->
            val animatedRadius = animation.getAnimatedValue("radius") as Float
            val animatedAlpha = animation.getAnimatedValue("transparency") as Float
            circle?.setDimensions(animatedRadius * 2)
            circle?.transparency = animatedAlpha
        })

        valueAnimator.start()

        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))

    }

}
