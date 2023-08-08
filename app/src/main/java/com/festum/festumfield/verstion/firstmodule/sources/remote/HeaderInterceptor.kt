package com.festum.festumfield.verstion.firstmodule.sources.remote

import com.festum.festumfield.MyApplication
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val original = chain.request()
        val builder = if (MyApplication.getAuthToken(MyApplication.context).isNotEmpty())
            MyApplication.getAuthToken(MyApplication.context).let {
                original.newBuilder()
                    .header("Authorization", it)
            }
        else
            original.newBuilder()
        val request = builder.build()
        return chain.proceed(request)

    }
}