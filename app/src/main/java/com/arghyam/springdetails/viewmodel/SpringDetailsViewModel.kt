package com.arghyam.springdetails.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.springdetails.repository.SpringDetailsRepository

class SpringDetailsViewModel : ViewModel() {

    private var springDetailsRepository: SpringDetailsRepository? = null
    val springDetailsData = MutableLiveData<ResponseModel>()
    val SpringDetailsError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setSpringDetailsRepository(springDetailsRepository: SpringDetailsRepository) {
        this.springDetailsRepository = springDetailsRepository
    }

    fun springDetailsApi(mContext: Context, requestModel: RequestModel) {
        springDetailsRepository!!.springDetailsApiRequest(mContext, requestModel, object :
            ResponseListener<ResponseModel> {
            override fun onSuccess(response: ResponseModel) {
                Log.d("success",response.toString())
                springDetailsData.value=response
            }

            override fun onError(error: String?) {
                SpringDetailsError.value=error
            }

            override fun onFailure(message: String?) {
                SpringDetailsError.value=message
            }

        })
    }

    fun getSpringDetailsResponse(): MutableLiveData<ResponseModel> {
        return springDetailsData
    }

    fun getSpringError(): SingleLiveEvent<String> {
        return SpringDetailsError
    }
}