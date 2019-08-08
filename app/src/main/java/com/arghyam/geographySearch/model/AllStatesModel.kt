package com.arghyam.geographySearch.model

data class AllStatesModel(var states: ArrayList<States>)

data class States(
    var states: String,
    var osid: String
)