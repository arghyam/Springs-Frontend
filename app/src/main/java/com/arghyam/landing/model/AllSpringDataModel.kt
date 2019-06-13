package com.arghyam.landing.model

class AllSpringDetailsModel(var springs: List<AllSpringDataModel>, var totalSprings: Int)

class AllSpringDataModel(var springCode : String,var springName : String, var userId: String, var tenantId :String, var orgId: String, var latitude: Double, var longitude: Double, var accuracy: Double, var village : String,var numberOfHouseholds: Int, var ownershipType: String, var usage: String, var images: List<String>, var extraInformation: SpringExtraInformationModel, var createdTimeStamp: String, var updatedTimeStamp: String)

class SpringExtraInformationModel(var osid: String)