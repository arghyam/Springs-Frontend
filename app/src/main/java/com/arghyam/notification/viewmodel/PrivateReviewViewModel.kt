package com.arghyam.notification.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.notification.repository.PrivateReviewRepository

class PrivateReviewViewModel : ViewModel()  {

    private var notificationRepository: PrivateReviewRepository? = null
    val privateReviewData = MutableLiveData<ResponseModel>()
    val privateReviewError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setPrivateReviewRepository(notificationRepository: PrivateReviewRepository) {
        this.notificationRepository = notificationRepository
    }

    fun privateReviewApi(requestModel: RequestModel) {
        notificationRepository!!.privateReviewApiRequest(requestModel, object :
            ResponseListener<ResponseModel> {
            override fun onSuccess(response: ResponseModel) {
                Log.d("success",response.toString())
                privateReviewData.value=response
            }

            override fun onError(error: String?) {
                privateReviewError.value=error
            }

            override fun onFailure(message: String?) {
                privateReviewError.value=message
            }

        })
    }

    fun getPrivateReviewResponse(): MutableLiveData<ResponseModel> {
        return privateReviewData
    }

    fun getPrivateReviewerError(): SingleLiveEvent<String> {
        return privateReviewError
    }

}