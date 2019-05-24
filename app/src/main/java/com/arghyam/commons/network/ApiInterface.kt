package com.arghyam.commons.network

import com.arghyam.commons.entities.ExampleEntity
import com.arghyam.iam.model.LoginRequestModel
import com.arghyam.iam.model.LoginResponseModel
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @GET("api/breed/hound/images")
    fun getImages(): Call<ExampleEntity>


    @POST("api/v1/user/login")
    fun userLogin(@Body requestModel: LoginRequestModel): Call<LoginResponseModel>

}