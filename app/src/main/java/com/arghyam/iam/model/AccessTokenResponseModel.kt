package com.arghyam.iam.model

open class AccessTokenModel(var accessTokenResponseDTO: AccessTokenResponseModel)

open class AccessTokenResponseModel(
    var access_token: String,
    var expires_in: String,
    var refresh_expires_in: String,
    var refresh_token: String,
    var user_id: String
)