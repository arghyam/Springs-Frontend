package com.arghyam.admin.model

data class RolesModel(val roles: UserRolesModel)

data class UserRolesModel(val userId:String,val role:String, val admin:String)