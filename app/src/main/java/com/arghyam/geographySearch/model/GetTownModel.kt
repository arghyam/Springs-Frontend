package com.arghyam.geographySearch.model

data class GetTownModel(var cities: TownRequest)
data class TownRequest(var fKeySubDistricts: String)