package com.arghyam.search.model

data class GetAllRecentSearchRequest(val recentSearches: RecentSearchRequest)
data class RecentSearchRequest(val userId: String)
