package com.arghyam.geographySearch.model

import com.google.gson.annotations.SerializedName

data class AllTownsModel(@SerializedName("cities") var town: ArrayList<Towns>)
data class Towns(@SerializedName("cities") var town: String,var osid: String)