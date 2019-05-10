package com.arghyam.iam.ui.profile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.arghyam.R
import com.arghyam.commons.utils.Constants.PHONE_NUMBER
import com.arghyam.landing.ui.activity.LandingActivity
import kotlinx.android.synthetic.main.content_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        init()
    }


    private fun init() {
        extractBundle()
        initSubmit()
    }


    private fun initSubmit() {
        submit.setOnClickListener {
            val intent = Intent(this@ProfileActivity, LandingActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /**
     * This method with get the phone number from previous screen and display in the edit text
     * @todo Assuming that 'phone' is the text passed from the previous screens
     */
    private fun extractBundle() {
        if (null != intent.extras) {
            val phone = intent.getStringExtra(PHONE_NUMBER)
            mobile.setText(phone)
        }
    }

}
