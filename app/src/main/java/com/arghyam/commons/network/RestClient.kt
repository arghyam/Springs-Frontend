package com.arghyam.commons.network

import android.content.Context
import android.util.Log
import com.arghyam.BuildConfig
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit


class RestClient {
    companion object {
        private var service: ApiInterface? = null

        private val clientLogin = OkHttpClient.Builder()
        private val loggingInterceptor = HttpLoggingInterceptor()


        init {
            try {
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                clientLogin.addInterceptor(loggingInterceptor)
                clientLogin.readTimeout(120, TimeUnit.SECONDS)
                    .connectTimeout(120, TimeUnit.SECONDS)
                clientLogin.addInterceptor { chain ->
                    var request = chain.request()
                    val url = request.url().newBuilder().build()
                    Log.d("version_url", url.toString())
                    request = request.newBuilder()
                        .build()
                    chain.proceed(request)
                }
            } catch (e: UnsupportedOperationException) {
                Log.e("exception", e.toString())
            }

        }

        fun getWebServiceData(): ApiInterface? {

            val mapper = ObjectMapper()
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            mapper.dateFormat = SimpleDateFormat()
            val retrofit: Retrofit

            if (BuildConfig.DEBUG) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .client(clientLogin.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            } else {
                retrofit = Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }

            service = retrofit.create<ApiInterface>(ApiInterface::class.java!!)
            return service
        }
    }


}
