package com.arghyam.more.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class UserProfileDataDetailsModel (var firstName: String,var username : String)