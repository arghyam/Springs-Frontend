package com.arghyam.additionalDetails.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.additionalDetails.repository.AdditionalDetailsRepository
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel

class AddAdditionalDetailsViewModel : ViewModel() {

    private var additionalDataRepository: AdditionalDetailsRepository? = null
    val mAdditionalData = MutableLiveData<ResponseModel>()
    val mAdditionalDataError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setAdditionalDataRepository(additionalDataRepository: AdditionalDetailsRepository) {
        this.additionalDataRepository = additionalDataRepository
    }

    fun addAdditionalDetailsApi(mContext: Context,springCode:String, requestModel: RequestModel) {
        additionalDataRepository!!.addAdditionalDataApiRequest(
            mContext,
            springCode,
            requestModel,
            object : ResponseListener<ResponseModel> {
                override fun onSuccess(response: ResponseModel) {
                    mAdditionalData.value = response
                }

                override fun onError(error: String?) {

                    mAdditionalDataError.value = error
                }

                override fun onFailure(message: String?) {
                    mAdditionalDataError.value = message
                }
            })
    }

    fun getAdditionalDataSuccess(): MutableLiveData<ResponseModel> {
        return mAdditionalData
    }

    fun getAdditionalDataError(): SingleLiveEvent<String> {
        return mAdditionalDataError
    }
}