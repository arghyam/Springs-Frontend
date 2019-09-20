package com.arghyam.deduplication.model

import java.io.Serializable

class DeduplicationModel(var springs: List<DeduplicationDataModel>) : Serializable

class DeduplicationDataModel(
    var springCode: String,
    var springName: String,
    var ownershipType: String,
    var images: List<String>,
    var address: String,
    var privateAccess : Boolean
) : Serializable