package com.arghyam.admin.viewmodel


import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.additionalDetails.repository.AdditionalDetailsRepository
import com.arghyam.admin.repository.GetRegisteredUsersRepository
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel

class GetRegisteredUsersViewModel : ViewModel() {

    private var getRegisteredUsersRepository: GetRegisteredUsersRepository? = null
    val mRegisteredUsers = MutableLiveData<ResponseModel>()
    val mRegisteredUsersError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setegisteredUsersRepository(getRegisteredUsersRepository: GetRegisteredUsersRepository) {
        this.getRegisteredUsersRepository = getRegisteredUsersRepository
    }

    fun getRegisteredUsersApi(mContext: Context) {
        getRegisteredUsersRepository!!.getRegisteredUsersApiRequest(
            mContext,
            object : ResponseListener<ResponseModel> {
                override fun onSuccess(response: ResponseModel) {
                    mRegisteredUsers.value = response
                }

                override fun onError(error: String?) {

                    mRegisteredUsersError.value = error
                }

                override fun onFailure(message: String?) {
                    mRegisteredUsersError.value = message
                }
            })
    }

    fun getAdditionalDataSuccess(): MutableLiveData<ResponseModel> {
        return mRegisteredUsers
    }

    fun getAdditionalDataError(): SingleLiveEvent<String> {
        return mRegisteredUsersError
    }
}