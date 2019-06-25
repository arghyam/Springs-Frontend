package com.arghyam.commons.network

import com.arghyam.addspring.model.UploadImageResponseModel
import com.arghyam.commons.entities.ExampleEntity
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

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

    @POST("api/v1/createSpring")
    fun createSpring(@Body createSpringRequestModel: RequestModel): Call<ResponseModel>

    @PUT("api/v1/user/profilePicture")
    @Multipart
    fun uploadImage(@Part file: MultipartBody.Part): Call<UploadImageResponseModel>

    @POST(" api/v1/createAdditionalInfo")
    fun uploadAdditionalData(@Body addAdditionalDetailsRequestModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/createDischargeData")
    fun uploadDischargeData(@Body dischargeDataRequestModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/getAllSprings")
    fun getAllSprings(@Query("pageNumber") pageNumber: Int ,@Body springRequestModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/springs")
    fun springDetails(@Body springDetailsRequestModel: RequestModel): Call<ResponseModel>

    @GET(" /api/v1/getRegisteredUsers")
    fun getRegisteredUsers(): Call<ResponseModel>

    @POST("/api/v1/user/getUserProfile")
    fun getUserProfile(@Body userProfileRequetModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/user/myActivities")
    fun getMyActivities(@Body userProfileRequetModel: RequestModel): Call<ResponseModel>

}