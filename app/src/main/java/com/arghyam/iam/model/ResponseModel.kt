package com.arghyam.iam.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties
open class ResponseModel(var id: String , var ver : String, var ets: String, var params: Params, var response: ResponseDataModel)