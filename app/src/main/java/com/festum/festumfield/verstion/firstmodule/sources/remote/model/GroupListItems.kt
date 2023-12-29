package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.lang.reflect.Member

data class GroupListItems(

    @field:SerializedName("aboutUs")
    val aboutUs: String? = null,

    @field:SerializedName("is_sender")
    val isSender: Boolean? = null,

    @field:SerializedName("is_pinned")
    val isPinned: Boolean? = null,

    @field:SerializedName("gender")
    val gender: String? = null,

    @field:SerializedName("nickName")
    val nickName: String? = null,

    @field:SerializedName("fullName")
    val fullName: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("emailId")
    val emailId: String? = null,

    @field:SerializedName("lastchatmessage")
    val lastMessage: GroupMessageItem? = null,

    @field:SerializedName("userName")
    val userName: String? = null,

    @field:SerializedName("profileimage")
    val profileimage: String? = null,

    @field:SerializedName("businessprofile")
    val businessprofile: BusinessProfile? = null,

    @field:SerializedName("areaRange")
    val areaRange: Int? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("is_business_profile_created")
    val isBusinessProfileCreated: Boolean? = null,

    @field:SerializedName("hobbies")
    val hobbies: List<String?>? = null,

    @field:SerializedName("contact_no")
    val contactNo: String? = null,

    @field:SerializedName("dob")
    val dob: String? = null,

    @field:SerializedName("socialMediaLinks")
    val socialMediaLinks: List<SocialMediaLinksItem?>? = null,

    @field:SerializedName("_id")
    var id: String? = null,

    @field:SerializedName("interestedin")
    val interestedin: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null,

    @field:SerializedName("timestamp")
    val timestamp: Long? = null,

    /* Custom Parameters*/
    @field:SerializedName("isNewMessage")
    var isNewMessage: Boolean? = null,

    @field:SerializedName("messageSize")
    val messageSize: String? = null,

    @field:SerializedName("isOnline")
    var online: Boolean? = null,

    @field:SerializedName("members")
    var members : ArrayList<MembersList>? = null,

    @field:SerializedName("unreadmessage_count")
    var unreadMessageCount : Int? = null,

) : Serializable