package com.arghyam.landing.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.landing.repository.PrivateAccessRepository

class PrivateAccessViewModel  : ViewModel()  {


    private var privateAccessRepository: PrivateAccessRepository? = null
    val privateAccessData = MutableLiveData<ResponseModel>()
    val privateAccessError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setPrivateAccessRepositoryRepository(notificationCountRepository: PrivateAccessRepository) {
        this.privateAccessRepository = notificationCountRepository
    }

    fun privateAccessApi(requestModel: RequestModel) {
        privateAccessRepository!!.privateSpringAccessApiRequest(requestModel, object :
            ResponseListener<ResponseModel> {
            override fun onSuccess(response: ResponseModel) {
                Log.d("successNotification",response.toString())
                privateAccessData.value=response
            }

            override fun onError(error: String?) {
                privateAccessError.value=error
            }

            override fun onFailure(message: String?) {
                privateAccessError.value=message
            }

        })
    }

    fun privateAccessResponse(): MutableLiveData<ResponseModel> {
        return privateAccessData
    }

    fun privateAccessError(): SingleLiveEvent<String> {
        return privateAccessError
    }
}