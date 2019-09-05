package com.arghyam.favourites.model

class FavSpringDetailsModel(var FavouriteSpring: List<FavSpringDataModel>)

class FavSpringDataModel(
    var springCode: String,
    var springName: String,
    var userId: String,
    var ownershipType: String,
    var images: List<String>,
    var address: String
)

