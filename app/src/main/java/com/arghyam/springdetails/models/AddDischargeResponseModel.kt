package com.arghyam.springdetails.models


class AddDischargeResponseModel(
    val springCode: String, val dischargeTime: ArrayList<String>, val volumeOfContainer: Float,
    val litresPerSecond: ArrayList<Float>,val userId: String, val status: String,val seasonality: String,val months: ArrayList<String>,val tenantId: String,
    val orgId: String,
    val images: ArrayList<String>,
    val createdTimeStamp: String,
    val updatedTimeStamp: String
)