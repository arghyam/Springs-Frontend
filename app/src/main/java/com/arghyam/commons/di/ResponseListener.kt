package com.arghyam.commons.di

import com.arghyam.iam.model.LoginRequestModel

interface ResponseListener<T> {
    fun onSuccess(successResponse: T)
    fun onError(error: String?)
    fun onFailure(message: String?)
}