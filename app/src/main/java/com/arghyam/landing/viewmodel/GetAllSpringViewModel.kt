package com.arghyam.landing.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.landing.repository.GetAllSpringRepository

class GetAllSpringViewModel: ViewModel()  {

    private var getAllSpringRepository: GetAllSpringRepository? = null
    val getAllSpringData = MutableLiveData<ResponseModel>()
    val getAllSpringError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setGetAllSpringRepository(createSpringRepository: GetAllSpringRepository) {
        this.getAllSpringRepository = getAllSpringRepository
    }

    fun getAllSpringApi(mContext: Context, requestModel: RequestModel) {
        getAllSpringRepository?.getAllSpringApiRequest(mContext, requestModel, object :
            ResponseListener<ResponseModel> {
            override fun onSuccess(response: ResponseModel) {
                Log.d("success",response.toString())
                getAllSpringData.value=response
            }

            override fun onError(error: String?) {
                getAllSpringError.value=error
            }

            override fun onFailure(message: String?) {
                getAllSpringError.value=message
            }

        })
    }

    fun getAllSpringResponse(): MutableLiveData<ResponseModel> {
        return getAllSpringData
    }

    fun getAllSpringError(): SingleLiveEvent<String> {
        return getAllSpringError
    }
}