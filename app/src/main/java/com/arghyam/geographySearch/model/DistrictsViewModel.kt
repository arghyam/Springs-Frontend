package com.arghyam.geographySearch.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.geographySearch.repository.DistrictsRepository
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel

class DistrictsViewModel : ViewModel() {
    private var districtsRepository: DistrictsRepository? = null
    val mDistricts = MutableLiveData<ResponseModel>()
    val mDistrictsError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setDistrictsRepository(districtsRepository: DistrictsRepository){
        this.districtsRepository = districtsRepository
    }

    fun myDistrictsApi(mContext: Context, requestModel: RequestModel) {
        districtsRepository!!.statesApiRequest(
            mContext,
            requestModel,
            object : ResponseListener<ResponseModel> {
                override fun onSuccess(response: ResponseModel) {
                    mDistricts.value = response
                }

                override fun onError(error: String?) {

                    mDistrictsError.value = error
                }

                override fun onFailure(message: String?) {
                    mDistrictsError.value = message
                }
            })
    }

    fun getDistrictsSuccess(): MutableLiveData<ResponseModel> {
        return mDistricts
    }

    fun getDistrictsError(): SingleLiveEvent<String> {
        return mDistrictsError
    }
}
