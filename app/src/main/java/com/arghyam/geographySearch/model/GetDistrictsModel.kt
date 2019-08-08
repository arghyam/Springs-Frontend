package com.arghyam.geographySearch.model

data class GetDistrictsModel(var districts: DistrictsRequest)
data class DistrictsRequest(var fKeyState: String)