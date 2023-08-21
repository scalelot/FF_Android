package com.festum.festumfield.verstion.firstmodule.viemodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.festum.festumfield.MyApplication
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.verstion.firstmodule.screens.BaseViewModel
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.FestumFieldApi
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.BusinessProfile
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ProfilePictureResponse
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ProfileResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONObject
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

    fun getProfile(){
        api.getProfile().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                profileData.value = resp.Data
            }, {
                profileData.value = null
            })
    }

    fun setProfilePicture(file: File?){

        if (file != null) {

            AndroidNetworking.upload(Constans.set_profile_pic).addMultipartFile("file", file)
                .addHeaders("Authorization", MyApplication.getAuthToken(MyApplication.context))
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

    fun getBusinessProfile(){
        api.getBusinessProfile().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                profileBusinessData.value = resp.Data
            }, {
                profileBusinessData.value = null
            })
    }

}