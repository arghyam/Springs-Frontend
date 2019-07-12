package com.arghyam.addspring.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.addspring.repository.GetSpringOptionalRepository
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel

class GetSpringOptionalViewModel: ViewModel() {


    private var getSpringOptionalRepository: GetSpringOptionalRepository? = null
    val getSpringOptionalData = MutableLiveData<ResponseModel>()
    val getSpringOptionalSpringError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setSpringOptionalRepository(getSpringOptionalRepository: GetSpringOptionalRepository) {
        this.getSpringOptionalRepository = getSpringOptionalRepository
    }

    fun springOptionalApi(mContext: Context, requestModel: RequestModel) {
        getSpringOptionalRepository!!.GetSpringOptionalApiRequest(mContext, requestModel, object :
            ResponseListener<ResponseModel> {
            override fun onSuccess(response: ResponseModel) {
                Log.d("success",response.toString())
                getSpringOptionalData.value=response
            }

            override fun onError(error: String?) {
                getSpringOptionalSpringError.value=error
            }

            override fun onFailure(message: String?) {
                getSpringOptionalSpringError.value=message
            }

        })
    }

    fun getSpringOptionalResponse(): MutableLiveData<ResponseModel> {
        return getSpringOptionalData
    }

    fun getSpringOptionalError(): SingleLiveEvent<String> {
        return getSpringOptionalSpringError
    }
}