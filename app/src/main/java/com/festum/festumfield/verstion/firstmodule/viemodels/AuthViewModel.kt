package com.festum.festumfield.verstion.firstmodule.viemodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.festum.festumfield.verstion.firstmodule.screens.BaseViewModel
import com.festum.festumfield.verstion.firstmodule.sources.local.model.GetFriendProduct
import com.festum.festumfield.verstion.firstmodule.sources.local.model.SendOtpBody
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.FestumFieldApi
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsProducts
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ProductResponse
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.SendOtpResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val api: FestumFieldApi
) : BaseViewModel() {

    var sendOtpData = MutableLiveData<SendOtpResponse?>()

    fun sendOtp(sendOtpBody: SendOtpBody){
        api.sendOtp(sendOtpBody).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                sendOtpData.value = resp.Data
            }, {
                sendOtpData.value = null
            })
    }

    /*fun getProduct(productId: String){
        api.getProductById(productId).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                productData.value = resp.Data
            }, {
                productData.value = null
            })
    }*/

}