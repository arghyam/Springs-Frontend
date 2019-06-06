package com.arghyam.springdetails.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.springdetails.repository.DischargeDataRepository

class AddDischargeDataViewModel : ViewModel() {
    private var dischargeDataRepository: DischargeDataRepository? = null
    val dischargeData = MutableLiveData<ResponseModel>()
    val dischargeDataError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setDischargeDataViewModel(dischargeDataRepository: DischargeDataRepository) {
        this.dischargeDataRepository = dischargeDataRepository
    }

    fun addDischargeApi(mContext: Context, requestModel: RequestModel) {
        dischargeDataRepository!!.addDischargeDataApiRequest(
            mContext,
            requestModel,
            object : ResponseListener<ResponseModel> {
                override fun onSuccess(response: ResponseModel) {
                    dischargeData.value = response
                }

                override fun onError(error: String?) {
                    dischargeDataError.value = error
                }

                override fun onFailure(message: String?) {
                    dischargeDataError.value = message
                }
            })
    }

    fun getDischargeSuccess(): MutableLiveData<ResponseModel> {
        return dischargeData
    }

    fun getDischargeError(): SingleLiveEvent<String> {
        return dischargeDataError
    }
}