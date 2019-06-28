package com.arghyam.addspring.interfaces

interface ImageUploadProgress {
    fun updateProgress(percent:Int,title: String,msg:String)
    fun onCancel(position: Int)
    fun retry(position: Int)
    fun onRemove(position: Int)
}