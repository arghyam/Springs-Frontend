package com.arghyam.iam.repository

import android.content.Context
import android.util.Log
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.network.RestClient
import com.arghyam.commons.utils.Constants
import com.arghyam.iam.model.LoginRequestModel
import com.arghyam.iam.model.LoginResponseModel
import com.arghyam.iam.model.VerifiyOtpRequestModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class IamRepository @Inject constructor() {

    fun LoginApiRequest(context: Context, requestModel: LoginRequestModel, responseListener: ResponseListener<LoginResponseModel>) {
        val loginCall = RestClient.getWebServiceData()?.userLogin(requestModel)
        loginCall?.enqueue(object : Callback<LoginResponseModel> {

            override fun onResponse(call: Call<LoginResponseModel>, response: Response<LoginResponseModel>) {
                if (null != response.body()) {
                    if (200 == response.code()) {
                        Log.d("success--","response code")
                        responseListener.onSuccess(response.body()!!)
                    } else {
                        Log.d("error---",response.code().toString())

                        responseListener.onError(response.code().toString() + "")
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                if (t is SocketTimeoutException) {
                    Log.d("failure","failure")
                    responseListener.onFailure(Constants.POOR_INTERNET_CONNECTION)
                } else if (t is UnknownHostException) {
                    responseListener.onFailure(Constants.POOR_INTERNET_CONNECTION)
                } else {
                    responseListener.onFailure(t.message)
                }
            }



        })
    }

    fun verifyOtp( requestModel: VerifiyOtpRequestModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}