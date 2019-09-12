package com.arghyam.admin.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class AllUsersDetailsModel(var users: List<AllUsersDataModel>)

@JsonIgnoreProperties(ignoreUnknown = true)
class AllUsersDataModel(
    var id: String?,
    var username: String?,
    var firstName: String?,
    var admin:Boolean,
    var reviewer: Boolean
)
