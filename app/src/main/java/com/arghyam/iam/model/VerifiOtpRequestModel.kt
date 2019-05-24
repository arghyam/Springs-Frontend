package com.arghyam.iam.model

import com.arghyam.BuildConfig

open class VerifiyOtpRequestModel (var id: String = BuildConfig.ID, var ver:String  = BuildConfig.VER, var ets: String  = BuildConfig.ETS, var params: Params, var request: Request)

