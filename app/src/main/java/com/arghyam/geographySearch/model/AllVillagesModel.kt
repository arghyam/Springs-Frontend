package com.arghyam.geographySearch.model

import com.google.gson.annotations.SerializedName

data class AllVillagesModel(@SerializedName("villages") var villages: ArrayList<Villages>)
data class Villages(
    @SerializedName("villages")
    var villages: String,
    @SerializedName("osid")
    var osid: String
)