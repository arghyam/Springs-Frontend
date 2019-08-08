package com.arghyam.geographySearch.model

import com.google.gson.annotations.SerializedName

data class AllBlocksModel (@SerializedName("subDistricts")var blocks: ArrayList<Blocks>)
data class Blocks(
    @SerializedName("subDistricts")
    var blocks : String,
    @SerializedName("osid")
    var osid : String
)