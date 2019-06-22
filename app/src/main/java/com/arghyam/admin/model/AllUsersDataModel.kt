package com.arghyam.admin.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class AllUsersDetailsModel(var users: List<AllUsersDataModel>)

@JsonIgnoreProperties(ignoreUnknown = true)
class AllUsersDataModel(
    var self: String,
    var id: String,
    var origin: String,
    var createdTimestamp: String,
    var username: String,
    var enabled: Boolean,
    var totp: Boolean,
    var emailVerified: Boolean,
    var firstName: String,
    var lastName: Int,
    var email: String,
    var federationLink: String,
    var serviceAccountClientId: String,
    var attributes: String,
    var credentials: String,
    var disableableCredentialTypes: List<DisabledCredentialsTypeModel>,
    var requiredActions: List<RequiredActionsModel>,
    var federatedIdentities: String,
    var realmRoles: String,
    var clientRoles: String,
    var clientConsents: String,
    var notBefore: Int,
    var applicationRoles: String,
    var socialLinks: String,
    var groups: String,
    var access: List<AccessDataModel>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AccessDataModel(
    var manageGroupMembership: Boolean,
    var view: Boolean,
    var mapRoles: Boolean,
    var impersonate: Boolean,
    var manage: Boolean
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DisabledCredentialsTypeModel(var disabledCredentials: String)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RequiredActionsModel(var actions: String)