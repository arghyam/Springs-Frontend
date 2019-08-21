package com.arghyam.landing.model

data class FavouritesModel(val favourites: AllFavouritesRequest)
data class AllFavouritesRequest(val springCode: String, val userId: String)