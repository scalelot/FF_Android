package com.festum.festumfield.verstion.firstmodule.sources.remote.di


import com.festum.festumfield.BuildConfig
import com.festum.festumfield.verstion.firstmodule.sources.remote.HeaderInterceptor
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.FestumFieldApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    companion object {
        var BASE_URL_RESOURCES = "https://api.festumfield.com/apis/v1/"
        var BASE_URL_SOCKET = "https://api.festumfield.com"
    }

    /*
    ** Api
    */

    @Provides
    @Singleton
    internal fun providePostcardApi(retrofit: Retrofit) = retrofit.create(FestumFieldApi::class.java)

    @Provides
    @Singleton
    internal fun provideRxRetrofit(
        rxCallAdapterFactory: RxJavaCallAdapterFactory,
        gsonConverterFactory: GsonConverterFactory,
        okHttpClient: OkHttpClient
    ) =
        Retrofit.Builder()
            .baseUrl(BASE_URL_RESOURCES)
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(rxCallAdapterFactory)
            .client(okHttpClient)
            .build()


    @Provides
    @Singleton
    internal fun provideRxCallAdapterFactory() = RxJavaCallAdapterFactory.create()

    @Provides
    @Singleton
    internal fun provideGson() = GsonConverterFactory.create()

    @Provides
    @Singleton
    internal fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        headerAuthInterceptor: HeaderInterceptor
    ) = OkHttpClient.Builder()
        .addInterceptor(headerAuthInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(2, TimeUnit.MINUTES)
        .writeTimeout(2, TimeUnit.MINUTES)
        .readTimeout(2, TimeUnit.MINUTES)
        .retryOnConnectionFailure(true)
        .build()

    @Provides
    @Singleton
    internal fun provideHeaderAuthInterceptor() = HeaderInterceptor()

    @Provides
    @Singleton
    internal fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level =
            if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE

        return httpLoggingInterceptor

    }


}