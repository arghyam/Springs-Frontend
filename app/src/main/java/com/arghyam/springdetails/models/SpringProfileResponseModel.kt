package com.arghyam.springdetails.models


import com.arghyam.iam.model.Params
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties
open class SpringProfileResponseModel(var id: String, var ver : String, var ets: String, var params: Params, var response: ResponseProfileDataModel)