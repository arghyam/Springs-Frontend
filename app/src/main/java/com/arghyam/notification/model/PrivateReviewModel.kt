package com.arghyam.notification.model

data class PrivateReviewModel(val Reviewer: PrivateReviewDataModel)

data class PrivateReviewDataModel(val status: String, val springCode: String,val osid: String, val adminId:String, val userId: String)