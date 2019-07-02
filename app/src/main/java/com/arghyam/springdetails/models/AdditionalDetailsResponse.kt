package com.arghyam.springdetails.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties
class AdditionalDetailsResponse(
    var springCode: String,
    var seasonality: String,
    var months: ArrayList<String>,
    var usage: ArrayList<String>,
    var numberOfHousehold: Int
)
