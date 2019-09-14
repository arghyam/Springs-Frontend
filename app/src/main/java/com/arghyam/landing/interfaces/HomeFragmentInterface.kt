package com.arghyam.landing.interfaces

interface HomeFragmentInterface {
    fun onFavouritesItemClickListener(springCode: String, userId: String, position: Int)
    fun onRequestAccess(springCode: String, userId: String)
}