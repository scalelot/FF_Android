package com.festum.festumfield.verstion.firstmodule.sources.local.model

import com.festum.festumfield.verstion.firstmodule.sources.remote.model.SocialMediaLinksItem
import com.google.gson.annotations.SerializedName

data class CreateProfileModel(

	@field:SerializedName("aboutUs")
	val aboutUs: String? = null,

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("nickName")
	val nickName: String? = null,

	@field:SerializedName("latitude")
	val latitude: Any? = null,

	@field:SerializedName("targetAudienceAgeMin")
	val targetAudienceAgeMin: Int? = null,

	@field:SerializedName("fullName")
	val fullName: String? = null,

	@field:SerializedName("emailId")
	val emailId: String? = null,

	@field:SerializedName("userName")
	val userName: String? = null,

	@field:SerializedName("areaRange")
	val areaRange: Int? = null,

	@field:SerializedName("hobbies")
	val hobbies: List<String?>? = null,

	@field:SerializedName("dob")
	val dob: String? = null,

	@field:SerializedName("targetAudienceAgeMax")
	val targetAudienceAgeMax: Int? = null,

	@field:SerializedName("socialMediaLinks")
	val socialMediaLinks: List<SocialMediaLinksItem?>? = null,

	@field:SerializedName("interestedin")
	val interestedin: String? = null,

	@field:SerializedName("longitude")
	val longitude: Any? = null
)