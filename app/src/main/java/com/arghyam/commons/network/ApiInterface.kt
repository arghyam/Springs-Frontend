package com.arghyam.commons.network

import com.arghyam.addspring.model.UploadImageResponseModel
import com.arghyam.commons.entities.ExampleEntity
import com.arghyam.geographySearch.model.AllStatesModel
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

    @PUT("/api/v1//users/profile/{userId}")
    fun updateUserProfile(@Path("userId") userId: String,@Body updateProfileRequestModel: RequestModel): Call<ResponseModel>

    @POST("api/v1/sendOTP")
    fun resendOtp(@Body requestOtpDataModel: RequestModel): Call<ResponseModel>

    @POST("api/v1/spring")
    fun createSpring(@Body createSpringRequestModel: RequestModel): Call<ResponseModel>

    @PUT("api/v1/user/profilePicture")
    @Multipart
    fun uploadImage(@Part file: MultipartBody.Part): Call<UploadImageResponseModel>

    @POST(" api/v1/springs/{springCode}/additionalInfo")
    fun uploadAdditionalData(@Path("springCode") springCode: String, @Body addAdditionalDetailsRequestModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/springs/{springCode}/discharge")
    fun uploadDischargeData(@Path("springCode") springCode: String, @Body dischargeDataRequestModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/getSprings")
    fun getAllSprings(@Query("pageNumber") pageNumber: Int ,@Body springRequestModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/getSprings")
    fun getAllSpringsOptional(@Body springOptionalRequestModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/springById")
    fun springDetails(@Body springDetailsRequestModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/getAdditionalDetailsForSpring")
    fun getAdditionalDetails(@Body springDetailsRequestModel: RequestModel): Call<ResponseModel>

    @GET(" /api/v1/users")
    fun getRegisteredUsers(): Call<ResponseModel>

    @POST("/api/v1/user/getUserProfile")
    fun getUserProfile(@Body userProfileRequetModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/user/myActivities")
    fun getMyActivities(@Body userProfileRequetModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/getStates")
    fun getAllStates(@Body statesModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/getDistrictsByStateOSID")
    fun getDistricts(@Body districtsModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/getSubDistrictsByDistrictOSID")
    fun getBlocks(@Body blocksModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/getVillagesBySubDistrictOSID")
    fun getVillages(@Body villagesModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/getCitiesBySubDistrictOSID")
    fun getTown(@Body citiesModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/reviewerData")
    fun reviewerData(@Body reviewerRequetModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/notifications/{userId}")
    fun notification(@Path("userId") userId: String, @Body notificationRequetModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/notificationCount/{userId}")
    fun notificationCount(@Path("userId") userId: String, @Body notificationCountRequetModel: RequestModel): Call<ResponseModel>

}