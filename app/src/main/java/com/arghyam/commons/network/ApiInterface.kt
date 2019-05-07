package com.arghyam.commons.network

import com.arghyam.commons.entities.ExampleEntity
import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {

    @GET("api/breed/hound/images")
    fun getImages(): Call<ExampleEntity>

}