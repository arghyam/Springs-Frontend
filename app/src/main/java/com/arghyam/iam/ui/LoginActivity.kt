package com.arghyam.iam.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arghyam.R
import com.arghyam.commons.utils.Constants.PHONE_NUMBER
import kotlinx.android.synthetic.main.content_login.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
    }

    private fun init () {
        initMobileInput()
        initGetOtp()
    }

    private fun initMobileInput() {
        inputNumber.addTextChangedListener(mobileNumberInputListener())
    }

    private fun initGetOtp() {
        sendOtpButton.setOnClickListener(getOtpOnClickListener())
    }

    private fun mobileNumberInputListener() : TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().length == 10) {
                    sendOtpButton.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
                } else {
                    sendOtpButton.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
                }
            }

        }
    }

    private fun getOtpOnClickListener() : View.OnClickListener {
        return View.OnClickListener {
            if(inputNumber.text.toString().length == 10) {
                var intent = Intent(this@LoginActivity, OtpVerifyActivity::class.java)
                intent.putExtra(PHONE_NUMBER, inputNumber.text.toString().trim())
                startActivity(intent)
            } else {
                Toast.makeText(this@LoginActivity, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
