package com.arghyam.favourites.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.favourites.repository.GetFavSpringsRepository
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel

class FavouritesViewModel : ViewModel(){

    private var getFavSpringsRepository: GetFavSpringsRepository? = null
    val getFavSpringData = MutableLiveData<ResponseModel>()
    val getFavSpringError: SingleLiveEvent<String> = SingleLiveEvent()


    fun setFavouritesRepository(favSpringsRepository: GetFavSpringsRepository) {
        this.getFavSpringsRepository = favSpringsRepository
    }

    fun storeFavouriteSpringsApi(mContext: Context, requestModel: RequestModel) {
        Log.e("fav----------", "$requestModel ============")
        getFavSpringsRepository?.storeFavSpringApiRequest(mContext, requestModel, object :
            ResponseListener<ResponseModel> {
            override fun onSuccess(response: ResponseModel) {
                Log.d("success",response.toString())
                getFavSpringData.value=response
            }

            override fun onError(error: String?) {
                getFavSpringError.value=error
            }

            override fun onFailure(message: String?) {
                getFavSpringError.value=message
            }

        })
    }

    fun getFavouriteSpringsApi(mContext: Context, requestModel: RequestModel) {
        Log.e("fav----------", "$requestModel ============")
        getFavSpringsRepository?.getFavSpringApiRequest(mContext, requestModel, object :
            ResponseListener<ResponseModel> {
            override fun onSuccess(response: ResponseModel) {
                Log.d("success",response.toString())
                getFavSpringData.value=response
            }

            override fun onError(error: String?) {
                getFavSpringError.value=error
            }

            override fun onFailure(message: String?) {
                getFavSpringError.value=message
            }

        })
    }
    fun favouritesResponse(): MutableLiveData<ResponseModel> {
        return getFavSpringData
    }

    fun favouritesError(): SingleLiveEvent<String> {
        return getFavSpringError
    }
}