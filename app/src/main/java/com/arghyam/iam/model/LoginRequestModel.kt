package com.arghyam.iam.model

import com.arghyam.BuildConfig

open class LoginRequestModel (var id: String = BuildConfig.ID, var ver:String  = BuildConfig.VER, var ets: String  = BuildConfig.ETS, var params: Params, var request: Request)


 class Request(var person: Person)

 class Person(var username: String)