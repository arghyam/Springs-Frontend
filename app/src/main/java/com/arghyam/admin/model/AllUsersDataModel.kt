package com.arghyam.admin.model

class AllUsersDetailsModel(var users: List<AllUsersDataModel>)

class AllUsersDataModel(var self : String,var id : String, var origin: String, var createdTimestamp :String, var username: String, var enabled: Boolean, var totp: Boolean, var emailVerified: Boolean, var firstName : String,var lastName: Int, var email: String, var federationLink: String, var serviceAccountClientId: String, var attributes: String, var credentials: String, var disableableCredentialTypes: List<DisabledCredentialsTypeModel> ,var requiredActions : List<RequiredActionsModel>,var federatedIdentities : String ,var realmRoles : String ,var clientRoles : String , var clientConsents : String , var notBefore :Int , var applicationRoles : String, var socialLinks : String, var groups: String , var access :List<AccessDataModel>)

data class AccessDataModel(var manageGroupMembership : Boolean ,var view : Boolean ,var mapRoles : Boolean ,var impersonate : Boolean ,var manage : Boolean )

data class DisabledCredentialsTypeModel(var disabledCredentials : String)

data class RequiredActionsModel (var actions :String)