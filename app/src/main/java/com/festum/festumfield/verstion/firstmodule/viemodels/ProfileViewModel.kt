package com.festum.festumfield.verstion.firstmodule.viemodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.verstion.firstmodule.screens.BaseViewModel
import com.festum.festumfield.verstion.firstmodule.sources.ApiBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.ChatPinBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.CreateBusinessProfileModel
import com.festum.festumfield.verstion.firstmodule.sources.local.model.CreateProfileModel
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.FestumFieldApi
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.BusinessProfile
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ProfilePictureResponse
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ProfileResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val api: FestumFieldApi
) : BaseViewModel() {

    var profileData = MutableLiveData<ProfileResponse?>()
    var profileBusinessData = MutableLiveData<BusinessProfile?>()
    var profilePictureData = MutableLiveData<ProfilePictureResponse?>()
    var documentData = MutableLiveData<ProfilePictureResponse?>()
    var createProfileData = MutableLiveData<ApiBody?>()
    var createBusinessProfileData = MutableLiveData<ApiBody?>()
    var setPinData = MutableLiveData<ApiBody?>()

    fun getProfile() {
        api.getProfile().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                profileData.value = resp.Data
            }, {
                profileData.value = null
            })
    }

    fun setProfilePicture(file: File?) {

        if (file != null) {

            AndroidNetworking.upload(Constans.set_profile_pic).addMultipartFile("file", file)
                .addHeaders("Authorization", AppPreferencesDelegates.get().token)
                .setPriority(Priority.HIGH).build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {

                        val profileImageData = response.getJSONObject("Data")
                        val profilePictureResponse = ProfilePictureResponse(
                            s3Url = profileImageData.optString("s3_url"),
                            key = profileImageData.optString("Key")
                        )

                        profilePictureData.value = profilePictureResponse

                    }

                    override fun onError(anError: ANError) {

                        profilePictureData.value = null

                    }

                })

        }

    }

    fun getBusinessProfile() {
        api.getBusinessProfile().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                profileBusinessData.value = resp.Data
            }, {
                profileBusinessData.value = null
            })
    }

    fun setBusinessProfilePicture(file: File?) {

        Log.e("TAG", "onResponse:$file")

        if (file != null) {

            AndroidNetworking.upload(Constans.set_business_profile_pic)
                .addMultipartFile("file", file)
                .addHeaders("Authorization", AppPreferencesDelegates.get().token)
                .setPriority(Priority.HIGH).build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {

                        val profileImageData = response.getJSONObject("Data")
                        val profilePictureResponse = ProfilePictureResponse(
                            s3Url = profileImageData.optString("s3_url"),
                            key = profileImageData.optString("Key")
                        )

                        profilePictureData.value = profilePictureResponse

                    }

                    override fun onError(anError: ANError) {


                        profilePictureData.value = null

                    }

                })

        }

    }

    fun setBusinessBrochure(file: File?) {

        if (file != null) {

            AndroidNetworking.upload(Constans.set_Setbrochure_pdf).addMultipartFile("file", file)
                .addHeaders("Authorization", AppPreferencesDelegates.get().token)
                .setPriority(Priority.HIGH).build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {

                        val profileImageData = response.getJSONObject("Data")
                        val profilePictureResponse = ProfilePictureResponse(
                            s3Url = profileImageData.optString("s3_url"),
                            key = profileImageData.optString("Key")
                        )
                        documentData.value = profilePictureResponse

                    }

                    override fun onError(anError: ANError) {

                        documentData.value = null

                    }

                })

        }

    }

    fun createProfile(profileData: CreateProfileModel, isBusinessClick: Boolean) {

        api.createPersonProfile(profileData).enqueue(object : Callback<ApiBody> {
            override fun onResponse(call: Call<ApiBody>, response: Response<ApiBody>) {

                if (isBusinessClick) {
                    if (response.isSuccessful) {
                        createBusinessProfileData.value = response.body()
                    } else {
                        val gson = Gson()
                        val type = object : TypeToken<ApiBody>() {}.type
                        val errorResponse: ApiBody? =
                            gson.fromJson(response.errorBody()?.charStream(), type)
                        createBusinessProfileData.value = errorResponse
                    }
                } else {
                    if (response.isSuccessful) {
                        createProfileData.value = response.body()
                    } else {
                        val gson = Gson()
                        val type = object : TypeToken<ApiBody>() {}.type
                        val errorResponse: ApiBody? =
                            gson.fromJson(response.errorBody()?.charStream(), type)
                        createProfileData.value = errorResponse
                    }
                }
            }

            override fun onFailure(call: Call<ApiBody>, t: Throwable) {

                if (isBusinessClick) {
                    createBusinessProfileData.value = null
                } else {
                    createProfileData.value = null
                }
            }
        })
    }

    fun createBusinessProfile(businessProfile: CreateBusinessProfileModel) {

        api.createBusinessProfile(businessProfile).enqueue(object : Callback<ApiBody> {
            override fun onResponse(call: Call<ApiBody>, response: Response<ApiBody>) {

                if (response.isSuccessful) {
                    createBusinessProfileData.value = response.body()
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<ApiBody>() {}.type
                    val errorResponse: ApiBody? =
                        gson.fromJson(response.errorBody()?.charStream(), type)
                    createBusinessProfileData.value = errorResponse
                }
            }

            override fun onFailure(call: Call<ApiBody>, t: Throwable) {
                createBusinessProfileData.value = null
            }

        })

    }

    fun setPin(itemData: FriendsListItems?, chatPin: Boolean) {

        val chatPinBody = ChatPinBody(userid = itemData?.id, isPinned = chatPin)
        api.sendPin(chatPinBody).enqueue(object : Callback<ApiBody> {
            override fun onResponse(call: Call<ApiBody>, response: Response<ApiBody>) {

                if (response.isSuccessful) {
                    setPinData.value = response.body()
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<ApiBody>() {}.type
                    val errorResponse: ApiBody? =
                        gson.fromJson(response.errorBody()?.charStream(), type)
                    setPinData.value = errorResponse
                }
            }

            override fun onFailure(call: Call<ApiBody>, t: Throwable) {
                setPinData.value = null
            }

        })

    }

}