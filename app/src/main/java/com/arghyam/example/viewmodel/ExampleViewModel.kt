package com.arghyam.example.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arghyam.commons.di.ResponseListener
import com.arghyam.commons.entities.ExampleEntity
import com.arghyam.commons.utils.SingleLiveEvent
import com.arghyam.example.repository.ExampleRepository

class ExampleViewModel : ViewModel() {

    private var exampleRepository: ExampleRepository? = null
    val exampleData = MutableLiveData<ExampleEntity>()
    val exampleError: SingleLiveEvent<String> = SingleLiveEvent()

    fun setExampleRepository(exampleRepository: ExampleRepository ) {
        this.exampleRepository = exampleRepository
    }

    fun getImagesApi() {
        exampleRepository!!.getDogImages(object : ResponseListener<ExampleEntity> {

            override fun onSuccess(successResponse: ExampleEntity?) {
                exampleData.value =successResponse
            }

            override fun onError(error: String?) {
                exampleError.value = error
            }

            override fun onFailure(message: String?) {
                exampleError.value = message
            }
        })
    }

    fun getImages(): MutableLiveData<ExampleEntity> {
        return exampleData
    }

    fun getImagesError(): SingleLiveEvent<String> {
        return exampleError
    }

}
