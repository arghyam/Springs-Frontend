package com.arghyam.landing.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LandingViewModel : ViewModel() {
    private var gpsEnabled= MutableLiveData<Boolean>()

    fun checkGpsStatus(status : Int){
        gpsEnabled.value=false
        if(status == -1){
            gpsEnabled.value=true
            Log.e("Guneet", "status"+status)

        }
    }

    fun getIsGpsEnabled(): LiveData<Boolean> {
        return gpsEnabled
    }
}