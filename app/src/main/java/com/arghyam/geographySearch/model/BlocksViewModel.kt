package com.arghyam.geographySearch.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.geographySearch.repository.BlocksRepository
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel

class BlocksViewModel : ViewModel() {
    private var blocksRepository: BlocksRepository? = null
    val mBlocks = MutableLiveData<ResponseModel>()
    val mBlocksError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setBlocksRepository(blocksRepository: BlocksRepository) {
        this.blocksRepository = blocksRepository
    }

    fun myBlocksApi(mContext: Context, requestModel: RequestModel) {
        blocksRepository!!.blocksApiRequest(
            mContext,
            requestModel,
            object : ResponseListener<ResponseModel> {
                override fun onSuccess(response: ResponseModel) {
                    mBlocks.value = response
                }

                override fun onError(error: String?) {

                    mBlocksError.value = error
                }

                override fun onFailure(message: String?) {
                    mBlocksError.value = message
                }
            })
    }

    fun getBlocksSuccess(): MutableLiveData<ResponseModel> {
        return mBlocks
    }

    fun getBlocksError(): SingleLiveEvent<String> {
        return mBlocksError
    }
}
