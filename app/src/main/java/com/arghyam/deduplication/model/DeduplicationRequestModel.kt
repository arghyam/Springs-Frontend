package com.arghyam.deduplication.model

data class DeduplicationRequestModel(var location: DeduplicationRequest)
data class DeduplicationRequest(var latitude: Double, var longitude: Double,var accuracy:Double)