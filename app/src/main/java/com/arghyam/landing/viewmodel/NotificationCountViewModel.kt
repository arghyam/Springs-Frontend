package com.arghyam.landing.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.landing.repository.NotificationCountRepository

class NotificationCountViewModel : ViewModel()  {


    private var notificationCountRepository: NotificationCountRepository? = null
    val notificationCountData = MutableLiveData<ResponseModel>()
    val notificationCountError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setNotificationCountRepository(notificationCountRepository: NotificationCountRepository) {
        this.notificationCountRepository = notificationCountRepository
    }

    fun notificationCountApi(mContext: Context, userId:String, requestModel: RequestModel) {
        notificationCountRepository!!.notificationCountApiRequest(mContext,userId, requestModel, object : ResponseListener<ResponseModel> {
            override fun onSuccess(response: ResponseModel) {
                Log.d("successNotification",response.toString())
                notificationCountData.value=response
            }

            override fun onError(error: String?) {
                notificationCountError.value=error
            }

            override fun onFailure(message: String?) {
                notificationCountError.value=message
            }

        })
    }

    fun getNotificationCountResponse(): MutableLiveData<ResponseModel> {
        return notificationCountData
    }

    fun notificationCountError(): SingleLiveEvent<String> {
        return notificationCountError
    }
}