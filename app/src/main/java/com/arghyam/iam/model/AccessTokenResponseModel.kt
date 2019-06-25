package com.arghyam.iam.model

open class AccessTokenModel(var accessTokenResponseDTO: AccessTokenResponseModel, var username:String,var userId: String)

open class AccessTokenResponseModel(
    var access_token: String,
    var expires_in: String,
    var refresh_expires_in: String,
    var refresh_token: String
)