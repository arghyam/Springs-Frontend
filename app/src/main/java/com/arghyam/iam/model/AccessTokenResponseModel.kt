package com.arghyam.iam.model

open class AccessTokenModel(var accessToken : AccessTokenResponseModel)

open class AccessTokenResponseModel(
    var access_token: String,
    var expires_in: String,
    var refresh_expires_in: String,
    var refresh_token: String
)