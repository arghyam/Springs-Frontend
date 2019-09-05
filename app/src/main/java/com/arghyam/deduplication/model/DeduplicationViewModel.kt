package com.arghyam.deduplication.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.deduplication.repository.DeduplicationRepository
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel

class DeduplicationViewModel : ViewModel() {
    private var getDeduplicationRepository: DeduplicationRepository? = null
    val getDeduplicationData = MutableLiveData<ResponseModel>()
    val getDeduplicationError: SingleLiveEvent<String> = SingleLiveEvent()


    fun setDeduplicationRepository(deduplicationRepository: DeduplicationRepository) {
        this.getDeduplicationRepository = deduplicationRepository
    }

    fun deduplicationSpringsApi(mContext: Context, requestModel: RequestModel) {
        Log.e("fav----------", "$requestModel ============")
        getDeduplicationRepository?.deduplicationApiRequest(
            mContext,
            requestModel,
            object : ResponseListener<ResponseModel> {
                override fun onSuccess(response: ResponseModel) {
                    Log.d("success", response.toString())
                    getDeduplicationData.value = response
                }

                override fun onError(error: String?) {
                    getDeduplicationError.value = error
                }

                override fun onFailure(message: String?) {
                    getDeduplicationError.value = message
                }

            })
    }

    fun deduplicationResponse(): MutableLiveData<ResponseModel> {
        return getDeduplicationData
    }

    fun deduplicationError(): SingleLiveEvent<String> {
        return getDeduplicationError
    }
}
