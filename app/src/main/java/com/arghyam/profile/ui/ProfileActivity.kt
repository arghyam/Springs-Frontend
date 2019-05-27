package com.arghyam.profile.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants.PHONE_NUMBER
import com.arghyam.landing.ui.activity.LandingActivity
import kotlinx.android.synthetic.main.content_otp_verify.*
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
        initNameEditText()
        initBackPressListener()

    }


    private fun initBackPressListener() {
        back_icon.setOnClickListener {
            finish()
        }
    }
    private fun initNameEditText() {
        fullName.addTextChangedListener(textChangedListener())
    }

    private fun initSubmit() {
        submit.setOnClickListener {
            if (fullName.text.length >= 3) {
                val intent = Intent(this@ProfileActivity, LandingActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                ArghyamUtils().longToast(this@ProfileActivity,"Please enter atleast 3 characters")
            }
        }
    }

    private fun textChangedListener(): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length!! >= 3) {
                    submit.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
                } else {
                    submit.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
                }
            }
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
