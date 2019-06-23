package com.arghyam.admin.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class AllUsersDetailsModel(var users: List<AllUsersDataModel>)

@JsonIgnoreProperties(ignoreUnknown = true)
class AllUsersDataModel(
    var self: String?,
    var id: String?,
    var origin: String?,
    var createdTimestamp: String?,
    var username: String?,
    var enabled: Boolean,
    var totp: Boolean,
    var emailVerified: Boolean,
    var firstName: String?,
    var lastName: String?,
    var email: String?,
    var federationLink: String?,
    var serviceAccountClientId: String?,
    var attributes: AttributesModel?,
    var credentials: String?,
    var disableableCredentialTypes: List<String>?,
    var requiredActions: List<String>?,
    var federatedIdentities: String?,
    var realmRoles: String?,
    var clientRoles: String?,
    var clientConsents: String?,
    var notBefore: Int?,
    var applicationRoles: String?,
    var socialLinks: String?,
    var groups: String?,
    var access: AccessDataModel?
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

@JsonIgnoreProperties(ignoreUnknown = true)
data class AttributesModel(var actions: String)