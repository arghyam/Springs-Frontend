package com.arghyam.splash.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.arghyam.R
import com.arghyam.iam.ui.LoginActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            val mainIntent = Intent(this, LoginActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, 2000)
    }

}
