package com.arghyam.commons.di

interface ResponseListener<T> {
    fun onSuccess(successResponse: T?)
    fun onError(error: String?)
    fun onFailure(message: String?)
}