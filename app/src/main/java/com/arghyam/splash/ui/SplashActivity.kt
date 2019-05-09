package com.arghyam.splash.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.arghyam.R
import com.arghyam.iam.ui.LoginActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            val titleView = findViewById<TextView>(R.id.title_name)
            val backgroundView = findViewById<ImageView>(R.id.startBackgroundAndroid)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val pairTitleView = Pair.create<View, String>(titleView, "titleTransitionName")
                val pairBackgroundView = Pair.create<View, String>(backgroundView, "loginBackgroundTransition")
                val pairs = ArrayList<Pair<View, String>>()
                pairs.add(pairTitleView)
                pairs.add(pairBackgroundView)
                val PairArray: Array<Pair<View, String>> = pairs.toTypedArray()
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@SplashActivity, *PairArray)
                val mainIntent = Intent(this, LoginActivity::class.java)
                startActivity(mainIntent, options.toBundle())
                finish()
            } else {
                val mainIntent = Intent(this, LoginActivity::class.java)
                startActivity(mainIntent)
                finish()
            }
        }, 2000)
    }

}
