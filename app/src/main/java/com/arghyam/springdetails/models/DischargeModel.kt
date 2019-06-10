package com.arghyam.springdetails.models

data class DischargeModel (val springCode: String, val dischargeTime:ArrayList<Float>, val volumeOfContainer: Float,
                           val litresPerSecond: ArrayList<Float>,val status: String)