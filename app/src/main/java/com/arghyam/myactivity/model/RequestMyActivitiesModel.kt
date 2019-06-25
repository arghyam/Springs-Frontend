package com.arghyam.myactivity.model

data class RequestMyActivitiesModel(var activities : MyActivitiesRequest)
data class MyActivitiesRequest(var userId :String)