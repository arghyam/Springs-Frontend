package com.arghyam.commons.network

import com.arghyam.commons.entities.ExampleEntity
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterface {

    @GET("api/breed/hound/images")
    fun getImages(): Call<ExampleEntity>


    @POST("api/v1/user/login")
    fun userLogin(@Body requestModel: RequestModel): Call<ResponseModel>

    @POST("api/v1/user/verifyOtp")
    fun verifyOtp(@Body verifyOtpRequestModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/user/updateUserProfile")
    fun updateUserProfile(@Body updateProfileRequestModel: RequestModel): Call<ResponseModel>

    @POST("api/v1/sendOTP")
    fun resendOtp(@Body requestOtpDataModel: RequestModel): Call<ResponseModel>


}