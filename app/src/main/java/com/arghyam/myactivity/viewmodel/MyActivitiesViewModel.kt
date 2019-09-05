package com.arghyam.myactivity.viewmodel
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.myactivity.repository.MyActivitiesRepository
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel

class MyActivitiesViewModel : ViewModel() {

    private var myActivitiesRepository: MyActivitiesRepository? = null
    val mMyActivities = MutableLiveData<ResponseModel>()
    val mMyActivitiesError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setMyActivitiesRepository(myActivitiesRepository: MyActivitiesRepository){
        this.myActivitiesRepository = myActivitiesRepository
    }

    fun myActivitiesApi(mContext: Context, userId:String,requestModel: RequestModel) {
        myActivitiesRepository!!.myActivitiesApiRequest(
            mContext,
            userId,
            requestModel,
            object : ResponseListener<ResponseModel> {
                override fun onSuccess(response: ResponseModel) {
                    mMyActivities.value = response
                }

                override fun onError(error: String?) {

                    mMyActivitiesError.value = error
                }

                override fun onFailure(message: String?) {
                    mMyActivitiesError.value = message
                }
            })
    }

    fun getMyActivitiesSuccess(): MutableLiveData<ResponseModel> {
        return mMyActivities
    }

    fun getMyActivitiesError(): SingleLiveEvent<String> {
        return mMyActivitiesError
    }
}