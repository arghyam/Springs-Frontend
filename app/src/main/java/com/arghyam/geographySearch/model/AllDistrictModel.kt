package com.arghyam.geographySearch.model

import com.google.gson.annotations.SerializedName

data class AllDistrictModel (var districts: ArrayList<Districts>)
data class Districts(
    var districts : String,
    @SerializedName ("osid")
    var osid : String
)