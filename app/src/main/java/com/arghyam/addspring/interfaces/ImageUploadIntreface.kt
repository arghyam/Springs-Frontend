package com.arghyam.addspring.interfaces

interface ImageUploadInterface {
    fun onSuccess(position: Int)
    fun onCancel(position: Int)
    fun retry(position: Int)
    fun onRemove(position: Int)
}