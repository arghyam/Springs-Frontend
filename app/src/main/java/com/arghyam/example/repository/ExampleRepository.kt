package com.arghyam.example.repository

import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.entities.ExampleEntity
import com.arghyam.commons.network.RestClient
import com.arghyam.commons.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class ExampleRepository @Inject constructor() {

    fun getDogImages(responseListener: ResponseListener<ExampleEntity>) {
        val cityCall = RestClient.getWebServiceData()?.getImages()
        cityCall?.enqueue(object : Callback<ExampleEntity> {
            override fun onResponse(call: Call<ExampleEntity>, response: Response<ExampleEntity>) {
                if (response.isSuccessful) {
                    if (null != response.body()) {
                        if (200 == response.code()) {
//                            responseListener.onSuccess(response.body())
                        } else {
                            responseListener.onError(response.code().toString() + "")
                        }
                    }
                } else {
                    responseListener.onError(response.code().toString() + "")
                }
            }

            override fun onFailure(call: Call<ExampleEntity>, t: Throwable) {
                if (t is SocketTimeoutException) {
                    responseListener.onFailure(Constants.POOR_INTERNET_CONNECTION)
                } else if (t is UnknownHostException) {
                    responseListener.onFailure(Constants.POOR_INTERNET_CONNECTION)
                } else {
                    responseListener.onFailure(t.message)
                }
            }
        })
    }

}