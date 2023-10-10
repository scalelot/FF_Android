package com.festum.festumfield.verstion.firstmodule.sources.remote.apis.searchcity

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://api.accuweather.com/"

    private var mInstance: RetrofitClient? = null
    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Synchronized
    fun getInstance(): RetrofitClient {
        if (mInstance == null) {
            mInstance = RetrofitClient
        }
        return mInstance as RetrofitClient
    }

    fun getModel(): Model {
        return retrofit.create(Model::class.java)
    }

}