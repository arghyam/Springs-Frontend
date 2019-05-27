package com.arghyam.profile.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.profile.repository.ProfileRepository

class ProfileViewModel : ViewModel() {

    private var profileRepository: ProfileRepository? = null
    val profileLoginData = MutableLiveData<ResponseModel>()
    val profileLoginError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setProfileRepository(profileRepository: ProfileRepository) {
        this.profileRepository = profileRepository
    }

    fun userProfileApi(mContext: Context, requestModel: RequestModel) {
        profileRepository!!.profileApiRequest(mContext, requestModel, object : ResponseListener<ResponseModel> {
            override fun onSuccess(response: ResponseModel) {
                Log.d("successProfile",response.toString())
                profileLoginData.value=response
            }

            override fun onError(error: String?) {
                profileLoginError.value=error
            }

            override fun onFailure(message: String?) {
                profileLoginError.value=message
            }

        })
    }

    fun getProfileResponse(): MutableLiveData<ResponseModel> {
        return profileLoginData
    }

    fun getProfileError(): SingleLiveEvent<String> {
        return profileLoginError
    }
}