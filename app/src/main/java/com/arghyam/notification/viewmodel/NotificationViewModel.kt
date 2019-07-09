package com.arghyam.notification.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.notification.repository.NotificationRepository

class NotificationViewModel : ViewModel()  {

    private var notificationRepository: NotificationRepository? = null
    val notificationData = MutableLiveData<ResponseModel>()
    val notificationError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setNotificationRepository(notificationRepository: NotificationRepository) {
        this.notificationRepository = notificationRepository
    }

    fun notificationApi(mContext: Context, requestModel: RequestModel) {
        notificationRepository!!.NotificationApiRequest(mContext, requestModel, object :
            ResponseListener<ResponseModel> {
            override fun onSuccess(response: ResponseModel) {
                Log.d("success",response.toString())
                notificationData.value=response
            }

            override fun onError(error: String?) {
                notificationError.value=error
            }

            override fun onFailure(message: String?) {
                notificationError.value=message
            }

        })
    }

    fun getNotificationResponse(): MutableLiveData<ResponseModel> {
        return notificationData
    }

    fun getNotifyError(): SingleLiveEvent<String> {
        return notificationError
    }

}