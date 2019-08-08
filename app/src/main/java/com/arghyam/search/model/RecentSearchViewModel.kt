package com.arghyam.search.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel

class RecentSearchViewModel : ViewModel() {
    private var recentSearchRepository: RecentSearchRepository? = null
    val mRecentSearches = MutableLiveData<ResponseModel>()
    val mRecentSearchesError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setRecentSearchRepository(statesRepository: RecentSearchRepository){
        this.recentSearchRepository = statesRepository
    }

    fun myRecentSearchesApi(mContext: Context, requestModel: RequestModel) {
        recentSearchRepository!!.recentSearchApiRequest(
            mContext,
            requestModel,
            object : ResponseListener<ResponseModel> {
                override fun onSuccess(response: ResponseModel) {
                    mRecentSearches.value = response
                }

                override fun onError(error: String?) {

                    mRecentSearchesError.value = error
                }

                override fun onFailure(message: String?) {
                    mRecentSearchesError.value = message
                }
            })
    }

    fun getRecentSearchesSuccess(): MutableLiveData<ResponseModel> {
        return mRecentSearches
    }

    fun getRecentSearchesError(): SingleLiveEvent<String> {
        return mRecentSearchesError
    }

}
