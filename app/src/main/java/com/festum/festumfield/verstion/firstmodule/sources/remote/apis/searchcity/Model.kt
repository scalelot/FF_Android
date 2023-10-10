package com.festum.festumfield.verstion.firstmodule.sources.remote.apis.searchcity

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface Model {

    @GET("locations/v1/cities/autocomplete")
    fun getSearch(
        @Header("Content-Type") header: String?,
        @Query("q") apiKey: CharSequence?,
        @Query("apikey") language: String?,
        @Query("language") details: String?,
        @Query("get_param") metric: String?
    ): Call<List<ResponseSearch?>?>?

}