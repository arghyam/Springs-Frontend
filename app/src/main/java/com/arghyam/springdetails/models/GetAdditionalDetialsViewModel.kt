package com.arghyam.springdetails.models

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.springdetails.repository.GetAdditionalDetailsRepository

class GetAdditionalDetialsViewModel : ViewModel(){

    private var getAdditionalDetailsRepository:GetAdditionalDetailsRepository? = null

    val getAdditionalDetailsData = MutableLiveData<ResponseModel>()
    val getAdditionalDetailsError: SingleLiveEvent<String> = SingleLiveEvent()
    val getAdditionalDetailsFailure: SingleLiveEvent<String> = SingleLiveEvent()

    fun setAdditionalDetailsRepository(getAdditionalDetailsRepository: GetAdditionalDetailsRepository) {
        this.getAdditionalDetailsRepository = getAdditionalDetailsRepository
    }

    fun getAdditionalDetailsApi(mContext:Context, requestModel: RequestModel){
        getAdditionalDetailsRepository!!.getAdditionalDetailsApiRequest(mContext, requestModel, object :
            ResponseListener<ResponseModel> {
            override fun onSuccess(response: ResponseModel) {
                Log.d("success",response.toString())
                getAdditionalDetailsData.value=response
            }

            override fun onError(error: String?) {
                getAdditionalDetailsError.value=error
            }

            override fun onFailure(message: String?) {
                getAdditionalDetailsFailure.value=message
            }

        }
        )
    }

    fun getAdditionalDetailsResponse(): MutableLiveData<ResponseModel> {
        return getAdditionalDetailsData
    }

    fun getAdditionalDetailsError(): SingleLiveEvent<String> {
        return getAdditionalDetailsError
    }
    fun getAdditionalDetailsFailure(): SingleLiveEvent<String> {
        return getAdditionalDetailsFailure
    }
}
