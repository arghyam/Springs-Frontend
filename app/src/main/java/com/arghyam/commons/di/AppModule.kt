package com.arghyam.commons.di

import android.content.Context
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig
import com.arghyam.additionalDetails.repository.AdditionalDetailsRepository
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.text.SimpleDateFormat
import javax.inject.Singleton

@Singleton
@Module
class AppModule(private val arghyamApplication: ArghyamApplication) {
    private var objectMapper: ObjectMapper? = null
    private var mHttpLoggingInterceptor: HttpLoggingInterceptor? = null
    private var builder: OkHttpClient.Builder? = null

    /**
     * This method will return the application context
     *
     * @return Application instance mmjpApplication
     */
    @Provides
    @Singleton
    fun provideContext(): Context {
        return arghyamApplication
    }


    /**
     * This method will return the application instance
     *
     * @return applications instance mmjpApplication
     */

    @Singleton
    @Provides
    fun provideApplication(): ArghyamApplication {
        return arghyamApplication
    }


    /**
     * this method gives the retrofit instance to the application
     *
     * @return Retrofit object
     */

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        builder = provideOkHttpClient()
        builder!!.addInterceptor(provideHttpLoggingInterceptorInstance())
        return Retrofit.Builder()
            .client(builder!!.build())
            .addConverterFactory(JacksonConverterFactory.create(providesJacksonFactoryInstance()))
            .baseUrl(BuildConfig.BASE_URL)
            .build()

    }


    /**
     * This method returns the httpclient for logging requestURLs
     */

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
    }


    /**
     * This method returns object mapper instance
     */
    @Singleton
    @Provides
    fun provideObjectMapper(): ObjectMapper {
        return ObjectMapper()
    }

    /**
     * This method returns object mapper instance
     */
    @Singleton
    @Provides
    fun providesJacksonFactoryInstance(): ObjectMapper {
        objectMapper = provideObjectMapper()
        objectMapper!!.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        objectMapper!!.dateFormat = providesSimpleDateFormatInstance()
        return objectMapper!!
    }


    /**
     * This method provides a simpleDateFormat instance
     *
     * @returnRouteSuggestionsRepository
     */
    @Provides
    fun providesSimpleDateFormatInstance(): SimpleDateFormat {
        return SimpleDateFormat()
    }


    /**
     * This method provides Httplogging Interceptor instance
     */

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptorInstance(): HttpLoggingInterceptor {
        mHttpLoggingInterceptor = HttpLoggingInterceptor()
        mHttpLoggingInterceptor!!.level = HttpLoggingInterceptor.Level.BODY
        return mHttpLoggingInterceptor!!
    }

    @Provides
    @Singleton
    fun providesAdditionalDetailsRepository(): AdditionalDetailsRepository {
        return AdditionalDetailsRepository()
    }

}
