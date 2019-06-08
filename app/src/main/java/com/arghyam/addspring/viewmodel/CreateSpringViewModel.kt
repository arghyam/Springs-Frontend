package com.arghyam.addspring.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.addspring.repository.CreateSpringRepository
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel


class CreateSpringViewModel : ViewModel() {


    private var createSpringRepository: CreateSpringRepository? = null
    val createSpringData = MutableLiveData<ResponseModel>()
    val createSpringError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setCreateSpringRepository(createSpringRepository: CreateSpringRepository) {
        this.createSpringRepository = createSpringRepository
    }

    fun createSpringApi(mContext: Context, requestModel: RequestModel) {
        createSpringRepository!!.CreateSpringApiRequest(mContext, requestModel, object : ResponseListener<ResponseModel> {
            override fun onSuccess(response: ResponseModel) {
                Log.d("success",response.toString())
                createSpringData.value=response
            }

            override fun onError(error: String?) {
                createSpringError.value=error
            }

            override fun onFailure(message: String?) {
                createSpringError.value=message
            }

        })
    }

    fun getCreateSpringResponse(): MutableLiveData<ResponseModel> {
        return createSpringData
    }

    fun getSpringError(): SingleLiveEvent<String> {
        return createSpringError
    }
}