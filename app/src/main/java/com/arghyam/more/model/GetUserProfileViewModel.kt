package com.arghyam.more.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.more.repository.GetUserProfileRepository

class GetUserProfileViewModel: ViewModel() {
    private var getUserProfileRepository: GetUserProfileRepository? = null
    val getUserProfileData = MutableLiveData<ResponseModel>()
    val getUserProfileError: SingleLiveEvent<String> = SingleLiveEvent()

    fun getUserProfileRepository(getUserProfileRepository: GetUserProfileRepository) {
        this.getUserProfileRepository = getUserProfileRepository
    }

    fun getUserProfileApi(mContext: Context, requestModel: RequestModel) {
        getUserProfileRepository?.getUserProfileApiRequest(mContext, requestModel, object :
            ResponseListener<ResponseModel> {
            override fun onSuccess(response: ResponseModel) {
                Log.d("success",response.toString())
                getUserProfileData.value=response
            }

            override fun onError(error: String?) {
                getUserProfileError.value=error
            }

            override fun onFailure(message: String?) {
                getUserProfileError.value=message
            }

        })
    }

    fun getUserProfileResponse(): MutableLiveData<ResponseModel> {
        return getUserProfileData
    }

    fun getUserProfileError(): SingleLiveEvent<String> {
        return getUserProfileError
    }
}
