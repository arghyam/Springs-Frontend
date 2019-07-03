package com.arghyam.search.viewmodel


import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.additionalDetails.repository.AdditionalDetailsRepository
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.search.repository.SearchRepository

class SearchViewModel : ViewModel()  {

    private var searchRepository: SearchRepository? = null
    val mSearchData = MutableLiveData<ResponseModel>()
    val mSearchDataError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setAdditionalDataRepository(searchRepository: SearchRepository) {
        this.searchRepository = searchRepository
    }

    fun searchApi(mContext: Context,springCode:String, requestModel: RequestModel) {
        searchRepository!!.searchDataApiRequest(
            mContext,
            springCode,
            requestModel,

            object : ResponseListener<ResponseModel> {
                override fun onSuccess(response: ResponseModel) {
                    mSearchData.value = response
                }

                override fun onError(error: String?) {

                    mSearchDataError.value = error
                }

                override fun onFailure(message: String?) {
                    mSearchDataError.value = message
                }
            })
    }

    fun getSearchDataSuccess(): MutableLiveData<ResponseModel> {
        return mSearchData
    }

    fun getSearchDataError(): SingleLiveEvent<String> {
        return mSearchDataError
    }
}