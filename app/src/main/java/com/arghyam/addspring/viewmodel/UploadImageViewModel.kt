package com.arghyam.addspring.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.addspring.model.UploadImageResponseModel
import com.arghyam.addspring.repository.UploadImageRepository
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import okhttp3.MultipartBody

class UploadImageViewModel: ViewModel()  {

    private var uploadImageRepository: UploadImageRepository? = null
    val uploadImageData = MutableLiveData<UploadImageResponseModel>()
    val uploadImageError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setUploadImageRepository(uploadImageRepository: UploadImageRepository) {
        this.uploadImageRepository = uploadImageRepository
    }

    fun uploadImageApi(mContext: Context, fileToUpload: MultipartBody.Part) {
        uploadImageRepository!!.UploadImageApiRequest(mContext, fileToUpload, object :
            ResponseListener<UploadImageResponseModel> {
            override fun onSuccess(response: UploadImageResponseModel) {
                Log.d("success",response.toString())
                uploadImageData.value=response
            }

            override fun onError(error: String?) {
                uploadImageError.value=error
            }

            override fun onFailure(message: String?) {
                uploadImageError.value=message
            }

        })
    }

    fun getUploadImageResponse(): MutableLiveData<UploadImageResponseModel> {
        return uploadImageData
    }

    fun getImageError(): SingleLiveEvent<String> {
        return uploadImageError
    }
}