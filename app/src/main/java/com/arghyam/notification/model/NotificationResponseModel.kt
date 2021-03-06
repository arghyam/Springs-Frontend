package com.arghyam.notification.model

class NotificationResponseModel(var notifications: List<AllNotificationModel>)

class AllNotificationModel(
    var userId: String,
    var springCode: String,
    var status: String,
    var dischargeDataOsid : String,
    var springName: String,
    var notificationTitle: String,
    var firstName: String,
    var createdAt: Long,
    var osid: String,
    var requesterId:String
    )