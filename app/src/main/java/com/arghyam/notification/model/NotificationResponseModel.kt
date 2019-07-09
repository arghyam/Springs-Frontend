package com.arghyam.notification.model

class NotificationResponseModel(var notifications: List<AllNotificationModel>)

class AllNotificationModel(
    var userId: String,
    var springCode: String,
    var status: String,
    var dischargeDataOsid : String,
    var springName: String,
    var userName: String,
    var createdAt: String
)