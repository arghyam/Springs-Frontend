package com.arghyam.addspring.interfaces

interface ImageUploadInterface {
    fun onCancel(position: Int)
    fun retry(position: Int)
    fun onRemove(position: Int)
}