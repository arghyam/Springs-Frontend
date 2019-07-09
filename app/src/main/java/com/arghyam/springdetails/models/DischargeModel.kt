package com.arghyam.springdetails.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties
data class DischargeModel(
    val springCode: String, val dischargeTime:ArrayList<String>, val volumeOfContainer: Float?,
    val litresPerSecond: ArrayList<Float>, val status: String, val seasonality:String, val months:ArrayList<String>, val images: ArrayList<String>, val userId : String)



data class DischargeDataModal (val date: String, val discharge: String, val submitted: String, val status: String)
