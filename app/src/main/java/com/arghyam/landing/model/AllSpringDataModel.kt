package com.arghyam.landing.model

class AllSpringDetailsModel(var springs: List<AllSpringDataModel>, var totalSprings: Int)

data class SearchModel(var springs: List<AllSpringDataModel>)

class AllSpringDataModel(
    var springCode: String, var springName: String, var userId: String, var tenantId:String, var orgId: String, var latitude: Double, var longitude: Double, var accuracy: Double, var address: String, var numberOfHouseholds: Int, var ownershipType: String, var usage: ArrayList<String>?, var images: List<String>, var extraInformation: SpringExtraInformationModel, var createdTimeStamp: String, var updatedTimeStamp: String,
    var isFavSelected: Boolean, var privateAccess: Boolean, var requested: Boolean)

class SpringExtraInformationModel(var osid: String)
