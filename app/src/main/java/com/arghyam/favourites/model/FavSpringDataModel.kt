package com.arghyam.favourites.model

import com.arghyam.landing.model.SpringExtraInformationModel

class FavSpringDetailsModel(var FavouriteSpring: List<FavSpringDataModel>)

class FavSpringDataModel(
    var springCode: String,
    var springName: String,
    var userId: String,
    var ownershipType: String,
    var images: List<String>,
    var address: String
)

