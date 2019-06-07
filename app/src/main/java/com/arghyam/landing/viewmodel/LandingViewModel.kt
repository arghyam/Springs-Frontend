package com.arghyam.landing.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LandingViewModel : ViewModel() {
    private var gpsEnabled= MutableLiveData<Boolean>()

    fun checkGpsStatus(status : Int){

        gpsEnabled.value=false
        if(status == -1){
            gpsEnabled.value=true
        }
    }

    fun getIsGpsEnabled(): LiveData<Boolean> {
        return gpsEnabled
    }
}