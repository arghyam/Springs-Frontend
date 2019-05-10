package com.arghyam.landing.services

interface SmsListener {
    fun onSuccess(code: String)
    fun onError()
}