package com.arghyam.iam.repository

import android.content.Context
import android.util.Log
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.network.RestClient
import com.arghyam.commons.utils.Constants
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class VerifyOtpRepository @Inject constructor() {

    fun verifyOtpRequest(
        context: Context,
        requestModel: RequestModel,
        responseListener: ResponseListener<ResponseModel>
    ) {
        val loginCall = RestClient.getWebServiceData()?.verifyOtp(requestModel)
        loginCall?.enqueue(object : Callback<ResponseModel> {

            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                if (null != response.body()) {
                    if (200 == response.code()) {
                        Log.d("success--", "response code")
                        if (response.body()?.response?.responseCode == "200") {
                            responseListener.onSuccess(response.body()!!)
                        } else {
                            responseListener.onError("Please enter correct Otp")
                        }

                    } else {
                        Log.d("error---", response.code().toString())
                        responseListener.onError(response.code().toString() + "")
                    }
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                when (t) {
                    is SocketTimeoutException -> {
                        Log.d("failure", "failure")
                        responseListener.onFailure(Constants.POOR_INTERNET_CONNECTION)
                    }
                    is UnknownHostException -> responseListener.onFailure(Constants.POOR_INTERNET_CONNECTION)
                    else -> responseListener.onFailure(t.message)
                }
            }


        })
    }
}