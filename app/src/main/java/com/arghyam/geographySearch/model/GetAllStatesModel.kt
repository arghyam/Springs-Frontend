package com.arghyam.geographySearch.model

import com.google.gson.annotations.SerializedName

data class GetAllStatesModel(val springs: AllStatesRequest) 
data class AllStatesRequest (@SerializedName("@type") val type : String)