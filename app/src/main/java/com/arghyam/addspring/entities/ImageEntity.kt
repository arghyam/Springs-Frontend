package com.arghyam.addspring.entities

import android.graphics.Bitmap
import android.util.Log

data class ImageEntity (var id: Int,var bitmap: Bitmap,var name: String, var uploadPercentage : Int){

    fun getProgress(): Int {
        Log.e("Anirudh","getprogress")
        return uploadPercentage
    }
}
