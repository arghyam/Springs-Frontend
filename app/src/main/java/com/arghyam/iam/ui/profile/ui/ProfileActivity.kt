package com.arghyam.iam.ui.profile.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.arghyam.R
import kotlinx.android.synthetic.main.content_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        extractBundle()
    }

    /**
     * This method with get the phone number from previous screen and display in the edit text
     * @todo Assuming that 'phone' is the text passed from the previous screens
     */
    private fun extractBundle() {
        if (null != intent.extras) {
            val phone = intent.getStringExtra(getString(R.string.profile_phone))
            mobile.setText(phone)
        }
    }

}
