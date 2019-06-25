package com.arghyam.addspring.model

class CreateSpringResponseObject(
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
    var usage: ArrayList<String>,
    var images: ArrayList<String>,
    var extraInformation: ExtraInfo,
    var createdTimeStamp: String,
    var updatedTimeStamp: String
)

class ExtraInfo(var extraInfo: String)