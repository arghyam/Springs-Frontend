package com.arghyam.iam.model

import com.arghyam.BuildConfig
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties
class LoginResponseModel (var id: String = BuildConfig.ID, var ver:String  = BuildConfig.VER , var ets: String  = BuildConfig.ETS,var params: Params,var response: Response)

 class Params(var did: String,var key: String,var msgid: String)

 class Response(var responseObject: ResponseObject,var reponseStatus: String,var responseCode:String)

 class ResponseObject(var newUserCreated: Boolean,var message: String)

