package com.arghyam.geographySearch.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.geographySearch.repository.StatesRepository
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel

class StatesViewModel : ViewModel() {
    private var statesRepository: StatesRepository? = null
    val mStates = MutableLiveData<ResponseModel>()
    val mStatesError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setStatesRepository(statesRepository: StatesRepository){
        this.statesRepository = statesRepository
    }

    fun myStatesApi(mContext: Context, requestModel: RequestModel) {
        statesRepository!!.statesApiRequest(
            mContext,
            requestModel,
            object : ResponseListener<ResponseModel> {
                override fun onSuccess(response: ResponseModel) {
                    mStates.value = response
                }

                override fun onError(error: String?) {

                    mStatesError.value = error
                }

                override fun onFailure(message: String?) {
                    mStatesError.value = message
                }
            })
    }

    fun getStatesSuccess(): MutableLiveData<ResponseModel> {
        return mStates
    }

    fun getStatesError(): SingleLiveEvent<String> {
        return mStatesError
    }

}
