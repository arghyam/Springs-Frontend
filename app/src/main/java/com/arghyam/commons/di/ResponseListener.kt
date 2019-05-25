package com.arghyam.commons.di

interface ResponseListener<T> {
    fun onSuccess(response: T)
    fun onError(error: String?)
    fun onFailure(message: String?)
}