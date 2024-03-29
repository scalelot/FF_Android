package com.festum.festumfield.verstion.firstmodule.screens.main.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
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
import com.festum.festumfield.verstion.firstmodule.sources.local.model.CreateProfileModel
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates.Companion.get
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.SocialMediaLinksItem
import com.festum.festumfield.verstion.firstmodule.utils.EventConstants
import com.festum.festumfield.verstion.firstmodule.utils.FileUtil
import com.festum.festumfield.verstion.firstmodule.utils.IntentUtil
import com.festum.festumfield.verstion.firstmodule.utils.IntentUtil.Companion.IS_EDIT_PROFILE
import com.festum.festumfield.verstion.firstmodule.viemodels.ProfileViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.swein.easyeventobserver.EventCenter
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageActivity
import com.theartofdev.edmodo.cropper.CropImageOptions
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import me.bendik.simplerangeview.SimpleRangeView
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class CreatePersonProfileActivity : BaseActivity<ProfileViewModel>(), TokenListener<Person>,
    OnMapReadyCallback {

    private lateinit var binding: ActivityCreateProfileBinding

    private var genSpinnerValue = ""
    private var radioGenderValue = "MALE"
    private var businessProfileValue = false
    private var hobbiesArrayList = ArrayList<String>()
    private var socialMediaLinkArrayList = ArrayList<SocialMediaLinksItem>()
    private var map: GoogleMap? = null
    private var isEditProfile = false

    override fun getContentView(): View {
        binding = ActivityCreateProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun setupUi() {

        /* Get Profile */
        viewModel.getProfile()

        val editProfile = intent.getStringExtra("EditProfile")
        if (editProfile?.isNotEmpty() == true) {
            binding.title.text = editProfile
            isEditProfile = true
        }


        /* Map View */
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        mapFragment?.getMapAsync(this)
        binding.mapview.getMapAsync(this)

        binding.icBack.setOnClickListener {
            finish()
        }

        val adapter =
            ArrayAdapter.createFromResource(this, R.array.gender, R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.genSpinner.adapter = adapter

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

        val people = Person.samplePeople()
        val personAdapter =
            PersonAdapter(this@CreatePersonProfileActivity, R.layout.person_layout, people)

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

        startTime.datePicker.maxDate = System.currentTimeMillis()

        binding.clickView.setOnClickListener {
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

            if (IntentUtil.readMapPermission(
                    this@CreatePersonProfileActivity
                ) && IntentUtil.readMapFinePermission(
                    this@CreatePersonProfileActivity
                )
            ) {
                onLocationCheck()
            } else
                AppPermissionDialog.showPermission(
                    this@CreatePersonProfileActivity,
                    title = getString(R.string.location_permission_title),
                    message = getString(R.string.location_permission)
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

        binding.rangeSeekbar.onRangeLabelsListener =
            object : SimpleRangeView.OnRangeLabelsListener {
                override fun getLabelTextForPosition(
                    rangeView: SimpleRangeView,
                    pos: Int,
                    state: SimpleRangeView.State
                ): String {
                    return pos.toString()
                }
            }

        binding.btnBusiness.setOnClickListener {

            socialMediaLink()

            val profileData = CreateProfileModel(
                fullName = binding.edtName.text.toString(),
                userName = binding.edtNickname.text.toString(),
                nickName = binding.edtNickname.text.toString(),
                emailId = binding.edtEmailId.text.toString(),
                areaRange = binding.tKm.text.toString().toInt(),
                aboutUs = binding.edtAboutUs.text.toString(),
                targetAudienceAgeMin = binding.txtMinAge.text.toString().toInt(),
                targetAudienceAgeMax = binding.txtMaxAge.text.toString().toInt(),
                hobbies = hobbiesArrayList,
                socialMediaLinks = socialMediaLinkArrayList,
                dob = binding.edtDob.text.toString(),
                gender = genSpinnerValue,
                latitude = Const.lattitude,
                longitude = Const.longitude,
                interestedin = radioGenderValue.uppercase()
            )

            if (profileData.fullName.isNullOrEmpty()) {
                binding.edtName.requestFocus()
                binding.edtName.error = "Enter Full Name"
                Snackbar.make(binding.relative, "Enter Full Name", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (profileData.userName.isNullOrEmpty()) {
                binding.edtNickname.requestFocus()
                binding.edtNickname.error = "Enter Nick Name"
                Snackbar.make(binding.relative, "Enter Nick Name", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (profileData.emailId.isNullOrEmpty()) {
                binding.edtEmailId.requestFocus()
                binding.edtEmailId.error = "Enter Email Id"
                Snackbar.make(binding.relative, "Enter Email Id", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (profileData.dob.isNullOrEmpty()) {
                Snackbar.make(binding.relative, "Enter Date of birth", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (profileData.aboutUs.isNullOrEmpty()) {
                binding.edtAboutUs.requestFocus()
                binding.edtAboutUs.error = "Enter About Us"
                Snackbar.make(binding.relative, "Enter About Us", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (Const.lattitude == null) {
                Snackbar.make(binding.relative, "Select Location", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (profileData.interestedin.isNullOrEmpty()) {
                Snackbar.make(binding.relative, "Select Interested In", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (Patterns.EMAIL_ADDRESS.matcher(binding.edtEmailId.text.toString()).matches()) {

                viewModel.createProfile(profileData, true)

            } else {
                Snackbar.make(binding.relative, "Please Enter Valid Email", Snackbar.LENGTH_SHORT)
                    .show()
            }


        }

        binding.btnSave.setOnClickListener {

            socialMediaLink()

            val interestedIn = if (radioGenderValue.isNullOrEmpty()){
                "MALE"
            }else{
                radioGenderValue.uppercase()
            }

            val profileData = CreateProfileModel(
                fullName = binding.edtName.text.toString(),
                userName = binding.edtNickname.text.toString(),
                nickName = binding.edtNickname.text.toString(),
                emailId = binding.edtEmailId.text.toString(),
                areaRange = binding.tKm.text.toString().toInt(),
                aboutUs = binding.edtAboutUs.text.toString(),
                targetAudienceAgeMin = binding.txtMinAge.text.toString().toInt(),
                targetAudienceAgeMax = binding.txtMaxAge.text.toString().toInt(),
                hobbies = hobbiesArrayList,
                socialMediaLinks = socialMediaLinkArrayList,
                dob = binding.edtDob.text.toString(),
                gender = genSpinnerValue,
                latitude = Const.lattitude,
                longitude = Const.longitude,
                interestedin = interestedIn
            )

            if (profileData.fullName.isNullOrEmpty()) {
                binding.edtName.requestFocus()
                binding.edtName.error = "Enter Full Name"
                Snackbar.make(binding.relative, "Enter Full Name", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            /*if (profileData.userName.isNullOrEmpty()) {
                binding.edtNickname.requestFocus()
                binding.edtNickname.error = "Enter Nick Name"
                Snackbar.make(binding.relative, "Enter Nick Name", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }*/

            if (profileData.emailId.isNullOrEmpty()) {
                binding.edtEmailId.requestFocus()
                binding.edtEmailId.error = "Enter Email Id"
                Snackbar.make(binding.relative, "Enter Email Id", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (profileData.dob.isNullOrEmpty()) {
                Snackbar.make(binding.relative, "Enter Date of birth", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (profileData.aboutUs.isNullOrEmpty()) {
                binding.edtAboutUs.requestFocus()
                binding.edtAboutUs.error = "Enter About Us"
                Snackbar.make(binding.relative, "Enter About Us", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            if (Const.lattitude == null) {
                Snackbar.make(binding.relative, "Select Location", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (profileData.interestedin.isNullOrEmpty()) {
                Snackbar.make(binding.relative, "Select Interested In", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (Patterns.EMAIL_ADDRESS.matcher(binding.edtEmailId.text.toString()).matches()) {

                viewModel.createProfile(profileData, false)

            } else {

                Snackbar.make(binding.relative, "Please Enter Valid Email", Snackbar.LENGTH_SHORT)
                    .show()

            }

        }

    }

    override fun setupObservers() {

        viewModel.profileData.observe(this) { profileData ->

            if (profileData != null) {

                Glide.with(this@CreatePersonProfileActivity)
                    .load(Constans.Display_Image_URL + profileData.profileimage)
                    .placeholder(R.drawable.ic_user)
                    .into(binding.profileImage)

               get().personalProfile = true

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

                if (profileData.areaRange == null) {
                    binding.tKm.text = "0"
                } else {
                    binding.tKm.text = profileData.areaRange.toString()
                }

                binding.seekbarRange.progress = profileData.areaRange ?: 0

                if (profileData.gender.equals("Male")) {
                    (binding.radioGender.getChildAt(0) as RadioButton).isChecked = true
                } else if (profileData.gender.equals("FEMALE")) {
                    (binding.radioGender.getChildAt(1) as RadioButton).isChecked = true
                } else if (profileData.gender.equals("OTHER")) {
                    (binding.radioGender.getChildAt(2) as RadioButton).isChecked = true
                }
                radioGenderValue = profileData.gender.toString()

                if (profileData.targetAudienceAgeMin == null && profileData.targetAudienceAgeMax == null) {
                    binding.txtMinAge.text = "0"
                    binding.txtMaxAge.text = "0"
                } else {
                    binding.txtMinAge.text = profileData.targetAudienceAgeMin.toString()
                    binding.txtMaxAge.text = profileData.targetAudienceAgeMax.toString()
                }

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

                if (profileData.isBusinessProfileCreated == true) {
                    businessProfileValue = true
                    binding.btnBusiness.visibility = View.GONE
                }

                /* Map View */

                Handler().postDelayed({
                    if (profileData.location?.coordinates?.isNotEmpty() == true) {
                        val long = profileData.location.coordinates[0]
                        val lat = profileData.location.coordinates[1]

                        if (lat != null && long != null) {
                            val latLng = LatLng(lat, long)
                            Const.lattitude = lat
                            Const.longitude = long
                            map?.addMarker(MarkerOptions().position(latLng))
                            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
                        }
                    }
                }, 500)


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

        viewModel.createProfileData.observe(this) {

            if (it != null) {
                Snackbar.make(binding.relative, it.message.toString(), Snackbar.LENGTH_SHORT).show()
            }

            if (it?.status == 200) {

                get().personalProfile = true

                val dictionary = mutableMapOf<String,Any>()

                dictionary["isEdit"] = "update"
                EventCenter.sendEvent(EventConstants.UPDATE_PERSON_PROFILE,this,dictionary)

                finish()

            }

        }

        viewModel.createBusinessProfileData.observe(this) {

            if (it != null) {
                Snackbar.make(binding.relative, it.message.toString(), Snackbar.LENGTH_SHORT).show()
            }

            if (it?.status == 200) {

                get().businessProfile = true

                /*val intent = Intent()
                intent.putExtra("EditBusinessProfile", true)
                setResult(1, intent)
                finish()*/

                startActivity(
                    Intent(
                        this@CreatePersonProfileActivity,
                        CreateBusinessProfileActivity::class.java
                    )
                )
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            Dexter.withContext(this@CreatePersonProfileActivity)
                .withPermissions(
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_AUDIO,
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                        if (permission?.areAllPermissionsGranted() == true) {
                            openIntent()
                        } else {
                            AppPermissionDialog.showPermission(
                                this@CreatePersonProfileActivity,
                                getString(R.string.media_permission),
                                getString(R.string.media_permission_title)
                            )
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

        } else {

            Dexter.withContext(this@CreatePersonProfileActivity)
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                        if (permission?.areAllPermissionsGranted() == true) {
                            openIntent()
                        } else {
                            AppPermissionDialog.showPermission(
                                this@CreatePersonProfileActivity,
                                getString(R.string.media_permission),
                                getString(R.string.media_permission_title)
                            )
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

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IntentUtil.IMAGE_PICKER_SELECT && resultCode == Activity.RESULT_OK) {

            val uri = IntentUtil.getPickImageResultUri(baseContext, data)

            if (uri != null) {

                cropImage(uri)

            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {

                if (result != null) {
                    val mImageFile = result.uri.let { it ->
                        FileUtil.getPath(Uri.parse(it.toString()), this@CreatePersonProfileActivity)
                            ?.let { File(it) }
                    }
                    val file = File(mImageFile.toString())
                    viewModel.setProfilePicture(file)
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(
                    this,
                    "Cropping failed" + result.error,
                    Toast.LENGTH_LONG
                ).show()

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
                val userLocation = LatLng(Const.lattitude, Const.longitude)
                map?.addMarker(MarkerOptions().position(userLocation))
                map?.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12f))
            }
        }

    }

    fun socialMediaLink() {

        if (binding.edtFb.text.toString() != "") {
            val faceBook =
                SocialMediaLinksItem(platform = "Facebook", link = binding.edtFb.text.toString())
            socialMediaLinkArrayList.add(faceBook)
        }

        if (binding.edtInsta.text.toString() != "") {
            val instagram = SocialMediaLinksItem(
                platform = "Instagram",
                link = binding.edtInsta.text.toString()
            )
            socialMediaLinkArrayList.add(instagram)
        }

        if (binding.edtTwitter.text.toString() != "") {
            val twitter = SocialMediaLinksItem(
                platform = "Twitter",
                link = binding.edtTwitter.text.toString()
            )
            socialMediaLinkArrayList.add(twitter)
        }

        if (binding.edtLinkdin.text.toString() != "") {
            val linkedin = SocialMediaLinksItem(
                platform = "Linkedin",
                link = binding.edtLinkdin.text.toString()
            )
            socialMediaLinkArrayList.add(linkedin)
        }

        if (binding.edtPintrest.text.toString() != "") {
            val pinterest = SocialMediaLinksItem(
                platform = "Pinterest",
                link = binding.edtPintrest.text.toString()
            )
            socialMediaLinkArrayList.add(pinterest)
        }

        if (binding.edtYoutube.text.toString() != "") {
            val youtube = SocialMediaLinksItem(
                platform = "Youtube",
                link = binding.edtYoutube.text.toString()
            )
            socialMediaLinkArrayList.add(youtube)
        }

    }

    private fun cropImage(uri: Uri) {
        val mOptions = CropImageOptions()
        mOptions.allowFlipping = false
        mOptions.allowRotation = false
        mOptions.aspectRatioX = 1
        mOptions.aspectRatioY = 1
        mOptions.cropShape = CropImageView.CropShape.OVAL
        val intent = Intent()
        intent.setClass(this@CreatePersonProfileActivity, CropImageActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable(CropImage.CROP_IMAGE_EXTRA_SOURCE, uri)
        bundle.putParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS, mOptions)
        intent.putExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE, bundle)
        startActivityForResult(
            intent,
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
        )
    }

    private fun onLocationCheck(){
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (_: Exception) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (_: Exception) {
        }

        if (!gps_enabled && !network_enabled) {

            MaterialAlertDialogBuilder(
                this,
                R.style.Body_ThemeOverlay_MaterialComponents_MaterialAlertDialog
            )
                .setTitle(getString(R.string.gps_enable))
                .setMessage(getString(R.string.turn_on_your_location))
                .setPositiveButton("OK") { dialogInterface, i ->
                    dialogInterface.dismiss()

                }
                .show()
        } else {
            startActivity(
                Intent(
                    this@CreatePersonProfileActivity,
                    MapsLocationActivity::class.java
                ).putExtra("isProfileLocation", true)
            )
        }

    }


}