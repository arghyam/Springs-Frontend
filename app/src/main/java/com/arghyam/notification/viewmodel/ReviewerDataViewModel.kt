package com.arghyam.notification.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.addspring.repository.CreateSpringRepository
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.notification.repository.ReviewerDataRepository

class ReviewerDataViewModel : ViewModel()  {


    private var reviewerDataRepository: ReviewerDataRepository? = null
    val reviewerData = MutableLiveData<ResponseModel>()
    val reviewerError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setReviewerDataRepository(reviewerDataRepository: ReviewerDataRepository) {
        this.reviewerDataRepository = reviewerDataRepository
    }

    fun reviewerApi(mContext: Context, requestModel: RequestModel) {
        reviewerDataRepository!!.reviewerDataApiRequest(mContext, requestModel, object :
            ResponseListener<ResponseModel> {
            override fun onSuccess(response: ResponseModel) {
                Log.d("success",response.toString())
                reviewerData.value=response
            }

            override fun onError(error: String?) {
                reviewerError.value=error
            }

            override fun onFailure(message: String?) {
                reviewerError.value=message
            }

        })
    }

    fun getReviewerDataResponse(): MutableLiveData<ResponseModel> {
        return reviewerData
    }

    fun getReviewError(): SingleLiveEvent<String> {
        return reviewerError
    }
}