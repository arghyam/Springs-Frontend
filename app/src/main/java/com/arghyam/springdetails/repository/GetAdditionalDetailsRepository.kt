package com.arghyam.springdetails.repository

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

class GetAdditionalDetailsRepository @Inject constructor() {
    fun getAdditionalDetailsApiRequest(context: Context,springCode:String, requestModel: RequestModel, responseListener: ResponseListener<ResponseModel>) {
        val springDetailsCall = RestClient.getWebServiceData()?.getAdditionalDetails(springCode,requestModel)
        springDetailsCall?.enqueue(object : Callback<ResponseModel> {

            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
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

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                when (t) {
                    is SocketTimeoutException -> {
                        Log.d("failure","failure")
                        responseListener.onFailure(Constants.POOR_INTERNET_CONNECTION)
                    }
                    is UnknownHostException -> responseListener.onFailure(Constants.POOR_INTERNET_CONNECTION)
                    else -> responseListener.onFailure(t.message)
                }
            }
        })
    }
}
