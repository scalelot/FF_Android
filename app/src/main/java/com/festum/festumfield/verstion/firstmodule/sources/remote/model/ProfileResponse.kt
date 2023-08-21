package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName

data class ProfileResponse(

	@field:SerializedName("aboutUs")
	val aboutUs: String? = null,

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("targetAudienceAgeMin")
	val targetAudienceAgeMin: Int? = null,

	@field:SerializedName("emailId")
	val emailId: String? = null,

	@field:SerializedName("last_sent_otp")
	val lastSentOtp: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("is_business_profile_created")
	val isBusinessProfileCreated: Boolean? = null,

	@field:SerializedName("contact_no")
	val contactNo: String? = null,

	@field:SerializedName("__v")
	val v: Int? = null,

	@field:SerializedName("targetAudienceAgeMax")
	val targetAudienceAgeMax: Int? = null,

	@field:SerializedName("socialMediaLinks")
	val socialMediaLinks: List<SocialMediaLinksItem?>? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null,

	@field:SerializedName("otp_timestamp")
	val otpTimestamp: Long? = null,

	@field:SerializedName("nickName")
	val nickName: String? = null,

	@field:SerializedName("fullName")
	val fullName: String? = null,

	@field:SerializedName("userName")
	val userName: String? = null,

	@field:SerializedName("profileimage")
	val profileimage: String? = null,

	@field:SerializedName("areaRange")
	val areaRange: Int? = null,

	@field:SerializedName("hobbies")
	val hobbies: List<String?>? = null,

	@field:SerializedName("dob")
	val dob: String? = null,

	@field:SerializedName("location")
	val location: Location? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("interestedin")
	val interestedin: String? = null,

	@field:SerializedName("fcmtoken")
	val fcmtoken: String? = null,

	@field:SerializedName("channelID")
	val channelID: String? = null
)

