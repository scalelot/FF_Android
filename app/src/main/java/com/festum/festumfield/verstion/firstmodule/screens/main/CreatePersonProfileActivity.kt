package com.festum.festumfield.verstion.firstmodule.screens.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.festum.festumfield.Activity.BusinessProfileActivity
import com.festum.festumfield.Activity.MapsLocationActivity
import com.festum.festumfield.R
import com.festum.festumfield.TagView.Person
import com.festum.festumfield.TagView.PersonAdapter
import com.festum.festumfield.TagView.TokenCompleteTextView
import com.festum.festumfield.TagView.TokenCompleteTextView.TokenListener
import com.festum.festumfield.Utils.Const
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.databinding.ActivityCreateProfileBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.screens.dialog.AppPermissionDialog
import com.festum.festumfield.verstion.firstmodule.utils.FileUtil
import com.festum.festumfield.verstion.firstmodule.utils.IntentUtil
import com.festum.festumfield.verstion.firstmodule.viemodels.ProfileViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import me.bendik.simplerangeview.SimpleRangeView
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class CreatePersonProfileActivity : BaseActivity<ProfileViewModel>(), TokenListener<Person>,
    OnMapReadyCallback{

    private lateinit var binding: ActivityCreateProfileBinding

    private var genSpinnerValue = ""
    private var radioGenderValue = ""
    private var businessProfileValue = false
    private var hobbiesArrayList = ArrayList<String>()
    private var map: GoogleMap? = null

    override fun getContentView(): View {
        binding = ActivityCreateProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun setupUi() {

        /* Get Profile */
        viewModel.getProfile()

        val editProfile = intent.getStringExtra("EditProfile")
        if (editProfile?.isNotEmpty() == true){
            binding.title.text = editProfile
        }



        /* Map View */
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        mapFragment?.getMapAsync(this)
        binding.mapview.getMapAsync(this)

        binding.icBack.setOnClickListener {
            finish()
        }

        val adapter = ArrayAdapter.createFromResource(this, R.array.gender, R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.genSpinner.adapter = adapter

        binding.genSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                genSpinnerValue = parent.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val people = Person.samplePeople()
        val personAdapter = PersonAdapter(this@CreatePersonProfileActivity, R.layout.person_layout, people)

        binding.tagView.setAdapter(personAdapter)
        binding.tagView.threshold = 1
        binding.tagView.setTokenListener(this@CreatePersonProfileActivity)
        binding.tagView.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select)
        binding.tagView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                (findViewById<View>(R.id.textValue) as TextView).text = editable.toString()
            }
        })

        binding.editProfile.setOnClickListener {
            if (IntentUtil.readPermission(
                    this@CreatePersonProfileActivity
                ) && IntentUtil.writePermission(
                    this@CreatePersonProfileActivity
                )
            ) {
                openIntent()
            } else
                onMediaPermission()
        }

        /* Birthdate picker */
        val newCalendar = Calendar.getInstance()
        val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        val startTime = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                val newDate = Calendar.getInstance()
                newDate[year, monthOfYear] = dayOfMonth
                binding.edtDob.setText(dateFormatter.format(newDate.time))
            }, newCalendar[Calendar.YEAR], newCalendar[Calendar.MONTH],
            newCalendar[Calendar.DAY_OF_MONTH]
        )

        binding.imgCalender.setOnClickListener {
            startTime.show()
        }

        binding.genSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                genSpinnerValue = parent.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.relativeMap.setOnClickListener {
            startActivity(
                Intent(this@CreatePersonProfileActivity, MapsLocationActivity::class.java).putExtra("isProfileLocation", true)
            )
        }


        binding.radioGender.setOnCheckedChangeListener { radioGroup, i ->
            val selectBtn = findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
            radioGenderValue = selectBtn.text.toString()
        }

        binding.seekbarRange.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var position = 0
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                position = i
                seekBar.progress = i
                binding.tKm.text = position.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                binding.tKm.text = position.toString()
            }
        })

        binding.rangeSeekbar.onTrackRangeListener = object : SimpleRangeView.OnTrackRangeListener {
            override fun onStartRangeChanged(rangeView: SimpleRangeView, start: Int) {
                binding.txtMinAge.text = start.toString()
            }

            override fun onEndRangeChanged(rangeView: SimpleRangeView, end: Int) {
                binding.txtMaxAge.text = end.toString()
            }
        }

        binding.rangeSeekbar.onRangeLabelsListener = object : SimpleRangeView.OnRangeLabelsListener {
            override fun getLabelTextForPosition(
                rangeView: SimpleRangeView,
                pos: Int,
                state: SimpleRangeView.State
            ): String {
                return pos.toString()
            }
        }

        binding.btnBusiness.setOnClickListener {

//            startActivity(Intent(this@CreatePersonProfileActivity, BusinessProfileActivity::class.java))
            startActivity(Intent(this@CreatePersonProfileActivity, CreateBusinessProfileActivity::class.java))


        }

        binding.btnSave.setOnClickListener {

            /*val profileData = CreateProfileModel(
                fullName = ,
                userName = ,
                nickName = ,
                emailId = ,
                areaRange = ,
                aboutUs =,
                targetAudienceAgeMin = ,
                targetAudienceAgeMax = ,
                hobbies = ,
                socialMediaLinks = ,
                dob = ,
                gender = ,
                latitude = ,
                longitude = ,
                interestedin =
            )*/
        }

    }

    override fun setupObservers() {

        viewModel.profileData.observe(this) { profileData ->

            if (profileData != null) {

                Glide.with(this@CreatePersonProfileActivity)
                    .load(Constans.Display_Image_URL + profileData.profileimage)
                    .placeholder(R.drawable.ic_user)
                    .into(binding.profileImage)

                binding.edtName.setText(profileData.fullName)
                binding.edtNickname.setText(profileData.nickName)
                binding.edtEmailId.setText(profileData.emailId)
                binding.edtDob.setText(profileData.dob)

                genSpinnerValue = profileData.gender.toString()
                val spinnerAdapter = binding.genSpinner.adapter as ArrayAdapter<String>
                val spinnerPosition = spinnerAdapter.getPosition(genSpinnerValue)
                binding.genSpinner.setSelection(spinnerPosition)

                binding.edtAboutUs.setText(profileData.aboutUs)

                profileData.hobbies?.forEach {
                    binding.tagView.addObjectAsync(Person(it))
                }

                binding.tKm.text = profileData.areaRange.toString()
                binding.seekbarRange.progress = profileData.areaRange ?: 0

                if (profileData.gender.equals("Male")){
                    (binding.radioGender.getChildAt(0) as RadioButton).isChecked = true
                }else if (profileData.gender.equals("FEMALE")){
                    (binding.radioGender.getChildAt(1) as RadioButton).isChecked = true
                }else if (profileData.gender.equals("OTHER")){
                    (binding.radioGender.getChildAt(2) as RadioButton).isChecked = true
                }

                binding.txtMinAge.text = profileData.targetAudienceAgeMin.toString()
                binding.txtMaxAge.text = profileData.targetAudienceAgeMax.toString()
                binding.rangeSeekbar.start = profileData.targetAudienceAgeMin ?: 0
                binding.rangeSeekbar.end = profileData.targetAudienceAgeMax ?: 0

                if (profileData.socialMediaLinks != null) {

                    profileData.socialMediaLinks.forEach {

                        when (it?.platform) {
                            "Facebook" -> {
                                binding.edtFb.setText(it.link)
                            }
                            "Instagram" -> {
                                binding.edtInsta.setText(it.link)
                            }
                            "Twitter" -> {
                                binding.edtTwitter.setText(it.link)
                            }
                            "Linkedin" -> {
                                binding.edtLinkdin.setText(it.link)
                            }
                            "Pinterest" -> {
                                binding.edtPintrest.setText(it.link)
                            }
                            "Youtube" -> {
                                binding.edtYoutube.setText(it.link)
                            }
                        }
                    }

                }

                if (profileData.isBusinessProfileCreated == true){
                    businessProfileValue = true
                    binding.btnBusiness.visibility = View.GONE
                }

                /* Map View */

                Handler().postDelayed({
                    if (profileData.location?.coordinates?.isNotEmpty() == true) {
                        val long = profileData.location.coordinates[0]
                        val lat = profileData.location.coordinates[1]

                        if (lat != null && long != null){
                            val latLng = LatLng(lat,long)
                            map?.addMarker(MarkerOptions().position(latLng))
                            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
                        }
                    }
                },500)


            }

        }

        viewModel.profilePictureData.observe(this) { profilePictureData ->

            if (profilePictureData != null) {

                val profileImage = profilePictureData.s3Url + profilePictureData.key

                Glide.with(this@CreatePersonProfileActivity)
                    .load(profileImage)
                    .placeholder(R.drawable.ic_user)
                    .into(binding.profileImage)

            } else {
                Toast.makeText(this, "Something went wrong ", Toast.LENGTH_SHORT).show()
            }

        }

    }

    @SuppressLint("SetTextI18n")
    override fun onTokenAdded(token: Person) {
        (findViewById<View>(R.id.lastEvent) as TextView).text = "Added: $token"
        updateTokenConfirmation()
    }

    @SuppressLint("SetTextI18n")
    override fun onTokenRemoved(token: Person) {
        (findViewById<View>(R.id.lastEvent) as TextView).text = "Removed: $token"
        updateTokenConfirmation()
    }

    @SuppressLint("SetTextI18n")
    override fun onTokenIgnored(token: Person) {
        (findViewById<View>(R.id.lastEvent) as TextView).text = "Ignored: $token"
        updateTokenConfirmation()
    }

    private fun updateTokenConfirmation() {
        if (hobbiesArrayList.isNotEmpty()) {
            hobbiesArrayList.clear()
        }
        val sb = StringBuilder()
        for (token in binding.tagView.objects) {
            sb.append(token.toString())
            sb.append(",")
            hobbiesArrayList.add(token.toString())
        }
        Const.tag_str = sb.toString()
    }

    private fun openIntent() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
        startActivityForResult(intent, IntentUtil.IMAGE_PICKER_SELECT)
    }

    private fun onMediaPermission() {

        Dexter.withContext(this@CreatePersonProfileActivity)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                    if (permission?.areAllPermissionsGranted() == true){
                        openIntent()
                    } else {
                        AppPermissionDialog.showPermission(this@CreatePersonProfileActivity,getString(R.string.media_permission),getString(R.string.media_permission_title))
                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            }).withErrorListener {}

            .check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IntentUtil.IMAGE_PICKER_SELECT && resultCode == Activity.RESULT_OK) {

            val uri = IntentUtil.getPickImageResultUri(baseContext, data)

            if (uri != null) {
                val mImageFile = uri.let { it ->
                    FileUtil.getPath(Uri.parse(it.toString()), this@CreatePersonProfileActivity)
                        ?.let { File(it) }
                }
                val file = File(mImageFile.toString())
                viewModel.setProfilePicture(file)
            }

        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
            map = googleMap
    }
    override fun onResume() {
        super.onResume()
        if (Const.longitude != null && Const.lattitude != null) {
            if (map != null) {
                map?.clear()
                if (Const.mCurrLocationMarker != null) {
                    Const.mCurrLocationMarker.remove()
                }
                val userLocation = LatLng(Const.lattitude,Const.longitude)
                map?.addMarker(MarkerOptions().position(userLocation))
                map?.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12f))
            }
        }

    }

}