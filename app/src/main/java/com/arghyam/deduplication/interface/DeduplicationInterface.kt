package com.arghyam.deduplication.`interface`

interface DeduplicationInterface {
    fun onRequestAccess(springCode: String, userId: String)
}
