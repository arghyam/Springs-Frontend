package com.arghyam.myactivity.model

data class AllActivitiesModel(var activities: ArrayList<Activities>)

data class Activities(
    var userId: String,
    var action: String,
    var createdAt: Long,
    var springName: String,
    var latitude: Float,
    var longitude: Float,
    var springCode: String
)