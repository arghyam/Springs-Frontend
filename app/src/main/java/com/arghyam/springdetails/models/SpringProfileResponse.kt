package com.arghyam.springdetails.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties
class SpringProfileResponse(

    var elevation: String,
    var images: List<String>, var type: String,
    var crtdDttm: String,
    var usage: String,
    var latitude: String, var springName: String, var ownershipType: String,
    var organization: String, var tenantId: String, var springCode: String,
    var village: String, var uploadedBy: String, var longitude: String,
    var updtDttm: String, var osid: String
)