package com.arghyam.addspring.repository

import android.content.Context
import android.util.Log
import com.arghyam.addspring.model.UploadImageResponseModel
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.network.RestClient
import com.arghyam.commons.utils.Constants
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class UploadImageRepository @Inject constructor() {

    fun UploadImageApiRequest(context: Context, fileToUpload:  MultipartBody.Part, responseListener: ResponseListener<UploadImageResponseModel>) {
        val uploadImageCall = RestClient.getWebServiceData()?.uploadImage(fileToUpload)
        uploadImageCall?.enqueue(object : Callback<UploadImageResponseModel> {

            override fun onResponse(call: Call<UploadImageResponseModel>, response: Response<UploadImageResponseModel>) {
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

            override fun onFailure(call: Call<UploadImageResponseModel>, t: Throwable) {
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