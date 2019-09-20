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

    @PUT("/api/v1//users/profile/{userId}")
    fun updateUserProfile(@Path("userId") userId: String, @Body updateProfileRequestModel: RequestModel): Call<ResponseModel>

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

    @POST("/api/v1/{userId}/getSprings")
    fun getAllSprings(@Path("userId") userId: String, @Query("pageNumber") pageNumber: Int, @Body springRequestModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/getSprings")
    fun getAllSpringsOptional(@Body springOptionalRequestModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/springById")
    fun springDetails(@Body springDetailsRequestModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/springs/{springCode}/additionaldetails")
    fun getAdditionalDetails(@Path("springCode") springCode: String, @Body springDetailsRequestModel: RequestModel): Call<ResponseModel>

    @GET(" /api/v1/users")
    fun getRegisteredUsers(): Call<ResponseModel>

    @POST("/api/v1/user/getUserProfile")
    fun getUserProfile(@Body userProfileRequetModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/user/{userId}/activities")
    fun getMyActivities(@Path("userId") userId: String, @Body userProfileRequetModel: RequestModel): Call<ResponseModel>

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

    @POST("/api/v1/users/{userId}/notifications")
    fun notification(@Path("userId") userId: String, @Body notificationRequetModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/users/{userId}/notificationCount")
    fun notificationCount(@Path("userId") userId: String, @Body notificationCountRequetModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/search")
    fun search(@Body searchRequestModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/getRecentSearches")
    fun getRecentSearches(@Body recentSearchesModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/users/getFavourites")
    fun getFavourites(@Body favouritesModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/users/favourites")
    fun storeFavourites(@Body favouritesModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/{userId}/searchByLocation")
    fun deduplication(@Path("userId") userId: String, @Body deduplicationModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/user/roles")
    fun assignRoles(@Body assignRolesModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/user/privateSpringAccess")
    fun privateSpringAccess(@Body privateSpringModel: RequestModel): Call<ResponseModel>

    @POST("/api/v1/user/reviewPrivateAccess")
    fun privateSpringReview(@Body privateSpringModel: RequestModel): Call<ResponseModel>
}