package com.arghyam.splash.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig
import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.Constants.ACCESS_TOKEN
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.landing.ui.activity.LandingActivity
import com.arghyam.more.model.GetUserProfileModel
import com.arghyam.more.model.GetUserProfileViewModel
import com.arghyam.more.model.LoggedInUserProfileModel
import com.arghyam.more.model.UserProfileDataDetailsModel
import com.arghyam.more.repository.GetUserProfileRepository
import com.arghyam.profile.ui.ProfileActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {
    private lateinit var mainIntent: Intent

    private var getUserProfileViewModel: GetUserProfileViewModel? = null
    private lateinit var responseData: UserProfileDataDetailsModel

    @Inject
    lateinit var getUserProfileRepository: GetUserProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        init()
    }

    private fun init() {
        initComponent()
        initUser()
    }

    private fun initComponent() {
        (application as ArghyamApplication).getmAppComponent()?.inject(this)
    }

    private fun initUser() {
        initRepository()
        if (SharedPreferenceFactory(this@SplashActivity).getString(ACCESS_TOKEN)?.isNotEmpty()!! ||
            SharedPreferenceFactory(this).getString(Constants.USER_PHONE)?.isNotEmpty()!!)
            getUserProfileRequest()
        else{
            Handler().postDelayed({
                mainIntent = Intent(this, LoginActivity::class.java)
                startActivity(mainIntent)
                finish()
            }, 2000)
        }
        initGetUserProfile()
    }

    private fun initRepository() {
        getUserProfileViewModel = ViewModelProviders.of(this).get(GetUserProfileViewModel::class.java)
        getUserProfileViewModel?.getUserProfileRepository(getUserProfileRepository)
    }

    private fun getUserProfileRequest() {
        Log.e("Anirudh", this?.let { SharedPreferenceFactory(it).getString(Constants.USER_PHONE) })
        var getUserProfileObject = RequestModel(
            id = Constants.GET_USER_PROFILE,
            ver = BuildConfig.VER,
            ets = BuildConfig.ETS,
            params = Params(
                did = "",
                key = "",
                msgid = ""
            ),
            request = GetUserProfileModel(
                person = LoggedInUserProfileModel(
                    phonenumber = this?.let { SharedPreferenceFactory(it).getString(Constants.USER_PHONE) }!!
                )
            )
        )
        getUserProfileViewModel?.getUserProfileApi(this, getUserProfileObject)
    }

    private fun initGetUserProfile() {
        getUserProfileViewModel?.getUserProfileResponse()?.observe(this, Observer {
            saveUserProfileData(it)
        })
        getUserProfileViewModel?.getUserProfileError()?.observe(this, Observer {
            Log.e("error", it)
        })
        if (getUserProfileViewModel?.getUserProfileError()?.hasObservers()!!) {
            getUserProfileViewModel?.getUserProfileError()?.removeObservers(this)
        }
    }

    private fun saveUserProfileData(responseModel: ResponseModel) {
        if (responseModel.response.responseCode == "200") {
            responseData = Gson().fromJson(
                ArghyamUtils().convertToString(responseModel.response.responseObject),
                object : TypeToken<UserProfileDataDetailsModel>() {}.type
            )
            initHandle(responseData)
        }
    }

    private fun initHandle(responseData: UserProfileDataDetailsModel) {

        Log.e("splash",SharedPreferenceFactory(this).getString(ACCESS_TOKEN)?.isEmpty()!!.toString()
                +"    "+ SharedPreferenceFactory(this).getString(Constants.USER_PHONE)?.isEmpty()!!)
        Handler().postDelayed({
            if (SharedPreferenceFactory(this).getString(ACCESS_TOKEN)?.isEmpty()!! ||
                SharedPreferenceFactory(this).getString(Constants.USER_PHONE)?.isEmpty()!!) {
                Log.e("splash","if")
                mainIntent = Intent(this, LoginActivity::class.java)
            } else if (responseData.firstName == null || responseData.firstName == "") {
                Log.e("splash","else if")
                mainIntent = Intent(this, ProfileActivity::class.java)
                mainIntent.putExtra(
                    Constants.PHONE_NUMBER,
                    SharedPreferenceFactory(this@SplashActivity).getString(Constants.USER_PHONE)
                )
            } else {
                Log.e("splash","else")
                mainIntent = Intent(this, LandingActivity::class.java)
            }
            startActivity(mainIntent)
            finish()
        }, 2000)
    }
}
