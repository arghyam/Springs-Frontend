package com.arghyam.notification.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.google.gson.annotations.SerializedName

@JsonIgnoreProperties
class NotificationModel(@SerializedName("@type")val type: String)