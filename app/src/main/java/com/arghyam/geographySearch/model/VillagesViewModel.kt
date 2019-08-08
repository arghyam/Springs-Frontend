package com.arghyam.geographySearch.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel

class VillagesViewModel : ViewModel() {
    private var villagesRepository: VillagesRepository? = null
    val mVillages = MutableLiveData<ResponseModel>()
    val mVillagesError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setVillagesRepository(villagesRepository: VillagesRepository) {
        this.villagesRepository = villagesRepository
    }

    fun myVillagesApi(mContext: Context, requestModel: RequestModel) {
        villagesRepository!!.villagesApiRequest(
            mContext,
            requestModel,
            object : ResponseListener<ResponseModel> {
                override fun onSuccess(response: ResponseModel) {
                    mVillages.value = response
                }

                override fun onError(error: String?) {

                    mVillagesError.value = error
                }

                override fun onFailure(message: String?) {
                    mVillagesError.value = message
                }
            })
    }

    fun getVillagesSuccess(): MutableLiveData<ResponseModel> {
        return mVillages
    }

    fun getVillagesError(): SingleLiveEvent<String> {
        return mVillagesError
    }
}
