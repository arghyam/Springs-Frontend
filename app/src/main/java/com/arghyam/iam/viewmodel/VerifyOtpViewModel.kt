package com.arghyam.iam.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.iam.repository.VerifyOtpRepository


class VerifyOtpViewModel : ViewModel() {
    private var repository: VerifyOtpRepository? = null
    val verifyOtpData = MutableLiveData<ResponseModel>()
    val verifyOtpError: SingleLiveEvent<String> = SingleLiveEvent()

    val resendOtpData = MutableLiveData<ResponseModel>()
    val resendOtpError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setRepository(repository: VerifyOtpRepository) {
        this.repository = repository
    }

    fun verifyOtpApi(context: Context, request: RequestModel) {
        repository!!.verifyOtpRequest(context, request, object : ResponseListener<ResponseModel> {
            override fun onSuccess(response: ResponseModel) {
                verifyOtpData.value = response
            }

            override fun onError(error: String?) {
                verifyOtpError.value = error
            }

            override fun onFailure(message: String?) {
                verifyOtpError.value = message
            }

        })
    }

    fun verifyOtpResponse(): MutableLiveData<ResponseModel> {
        return verifyOtpData
    }

    fun verifyOtpError(): SingleLiveEvent<String> {
        return verifyOtpError
    }


    fun resendOtpApi(context: Context, request: RequestModel) {
        repository!!.resendOtpRequest(context, request, object : ResponseListener<ResponseModel> {
            override fun onSuccess(response: ResponseModel) {
                resendOtpData.value = response
            }

            override fun onError(error: String?) {
                resendOtpError.value = error
            }

            override fun onFailure(message: String?) {
                resendOtpError.value = message
            }

        })
    }

    fun resendOtpResponse(): MutableLiveData<ResponseModel> {
        return resendOtpData
    }

    fun resendOtpError(): SingleLiveEvent<String> {
        return resendOtpError
    }

}