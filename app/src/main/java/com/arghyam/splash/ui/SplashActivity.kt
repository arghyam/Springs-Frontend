package com.arghyam.splash.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.arghyam.R
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.Constants.ACCESS_TOKEN
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.landing.ui.activity.LandingActivity
import com.arghyam.profile.ui.ProfileActivity
import kotlinx.android.synthetic.main.content_login.*

class SplashActivity : AppCompatActivity() {
    private lateinit var mainIntent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Log.e("Anirudh",SharedPreferenceFactory(this@SplashActivity).getString(Constants.USER_PHONE)+"  fj")

        Handler().postDelayed({
            if (SharedPreferenceFactory(this@SplashActivity).getString(ACCESS_TOKEN) == "") {
                mainIntent = Intent(this, LoginActivity::class.java)
            }
            else if (SharedPreferenceFactory(this@SplashActivity).getString(Constants.USER_NAME)==null || SharedPreferenceFactory(this@SplashActivity).getString(Constants.USER_NAME)=="" ){
                mainIntent = Intent(this, ProfileActivity::class.java)
                mainIntent.putExtra(Constants.PHONE_NUMBER, SharedPreferenceFactory(this@SplashActivity).getString(Constants.USER_PHONE))
            }
            else {
                mainIntent = Intent(this, LandingActivity::class.java)
            }
            startActivity(mainIntent)
            finish()
        }, 2000)
    }

}
