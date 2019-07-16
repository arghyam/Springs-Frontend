package com.arghyam.springdetails.models


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.google.gson.annotations.SerializedName

@JsonIgnoreProperties
class SpringProfileResponse(
    var springCode: String,
    var springName: String,
    var userId: String,
    var tenantId: String,
    var orgId: String,
    var latitude: Double,
    var longitude: Double,
    var elevation: Double,
    var accuracy: Float,
    var village: String,
    var numberOfHouseholds: Int,
    var ownershipType: String,
    var usage: ArrayList<String>,
    var images: ArrayList<String>,
    var extraInformation: ExtraInfoDischarge,
    var createdTimeStamp: Long,
    var updatedTimeStamp: String,
    var submittedBy: String
)

class ExtraInfoDischarge(var dischargeData: ArrayList<DischargeData>)

class DischargeData(
    var dischargeTime: ArrayList<String>,
    var images: ArrayList<String>,
    var months: ArrayList<String>,
    @SerializedName("@type")
    val type: String,
    var updatedTimeStamp: String,
    var createdTimeStamp: Long,
    var userId: String,
    var orgId: String,
    var submittedby: String,
    var volumeOfContainer: Float,
    var litresPerSecond: ArrayList<Double>,
    var tenantId: String,
    var springCode: String,
    var seasonality: String,
    var status: String,
    var osid: String
)