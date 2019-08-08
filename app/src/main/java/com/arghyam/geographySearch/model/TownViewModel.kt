package com.arghyam.geographySearch.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel

class TownViewModel : ViewModel() {
    private var townRepository: TownRepository? = null
    val mTown = MutableLiveData<ResponseModel>()
    val mTownError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setTownRepository(townRepository: TownRepository) {
        this.townRepository = townRepository
    }

    fun myTownApi(mContext: Context, requestModel: RequestModel) {
        townRepository!!.townApiRequest(
            mContext,
            requestModel,
            object : ResponseListener<ResponseModel> {
                override fun onSuccess(response: ResponseModel) {
                    mTown.value = response
                }

                override fun onError(error: String?) {

                    mTownError.value = error
                }

                override fun onFailure(message: String?) {
                    mTownError.value = message
                }
            })
    }

    fun getTownSuccess(): MutableLiveData<ResponseModel> {
        return mTown
    }

    fun getTownError(): SingleLiveEvent<String> {
        return mTownError
    }
}
