package com.arghyam.admin.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.admin.repository.AssignRolesRepository
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel

class AssignRoleViewModel: ViewModel() {
    private var assignRolesRepository: AssignRolesRepository? = null
    val mAssignRole = MutableLiveData<ResponseModel>()
    val mAssignRoleError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setAssignRoleRepository(statesRepository: AssignRolesRepository){
        this.assignRolesRepository = statesRepository
    }

    fun assignRoleApi(mContext: Context, requestModel: RequestModel) {
        assignRolesRepository!!.assignRoleApiRequest(
            mContext,
            requestModel,
            object : ResponseListener<ResponseModel> {
                override fun onSuccess(response: ResponseModel) {
                    mAssignRole.value = response
                }

                override fun onError(error: String?) {

                    mAssignRoleError.value = error
                }

                override fun onFailure(message: String?) {
                    mAssignRoleError.value = message
                }
            })
    }

    fun assignRoleSuccess(): MutableLiveData<ResponseModel> {
        return mAssignRole
    }

    fun assignRoleError(): SingleLiveEvent<String> {
        return mAssignRoleError
    }

}
