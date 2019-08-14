package com.arghyam.geographySearch.interfaces

interface SearchInterface {
    fun getTitle(id: String, title: String,osid: String, type : Int)
    fun isClicked(click:Boolean, type : Int)
}