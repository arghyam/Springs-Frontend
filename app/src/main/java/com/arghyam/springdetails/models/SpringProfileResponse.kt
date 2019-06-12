package com.arghyam.springdetails.models


import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties
class SpringProfileResponse(
    var springCode: String,
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
    var usage: String,
    var images: ArrayList<String>,
    var extraInformation: ExtraInfoDischarge,
    var createdTimeStamp: String,
    var updatedTimeStamp: String
)

class ExtraInfoDischarge(var dischargeData: ArrayList<String>)