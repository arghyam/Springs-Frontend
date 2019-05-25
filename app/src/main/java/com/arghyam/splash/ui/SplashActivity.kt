package com.arghyam.splash.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.arghyam.R
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.Constants.ACCESS_TOKEN
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.landing.ui.activity.LandingActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var mainIntent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            val sharedPreference = getSharedPreferences(Constants.APPLICATION_PREFERENCE, Context.MODE_PRIVATE)
            mainIntent = if (sharedPreference.getString(ACCESS_TOKEN, null) == null) {
                Intent(this, LoginActivity::class.java)
            } else {
                Intent(this, LandingActivity::class.java)
            }
            startActivity(mainIntent)
            finish()
        }, 2000)
    }

}
