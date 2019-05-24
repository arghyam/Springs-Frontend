package com.arghyam.iam.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.iam.model.LoginRequestModel
import com.arghyam.iam.model.LoginResponseModel
import com.arghyam.iam.repository.IamRepository

class IamViewModel  : ViewModel() {
    private var iamRepository: IamRepository? = null
    val iamLoginData = MutableLiveData<LoginResponseModel>()
    val iamLoginError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setIamRepository(iamRepository: IamRepository) {
        this.iamRepository = iamRepository
    }

    fun userLoginApi(mContext:Context, requestModel: LoginRequestModel) {
        iamRepository!!.LoginApiRequest(mContext, requestModel, object : ResponseListener<LoginResponseModel> {
            override fun onSuccess(successResponse: LoginResponseModel) {
            Log.d("success",successResponse.toString())
                iamLoginData.value=successResponse
            }

            override fun onError(error: String?) {
                iamLoginError.value=error
            }

            override fun onFailure(message: String?) {
                iamLoginError.value=message
            }

        })
    }

    fun getLoginResponse(): MutableLiveData<LoginResponseModel> {
        return iamLoginData
    }

    fun getLoginError(): SingleLiveEvent<String> {
        return iamLoginError
    }
}