package com.arghyam.springdetails.models

data class DischargeModel(
    val springCode: String, val dischargeTime:ArrayList<Int>, val volumeOfContainer: Float?,
    val litresPerSecond: ArrayList<Float>, val status: String, val images: ArrayList<String> )