package com.arghyam.iam.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arghyam.R
import com.arghyam.commons.utils.AppSignatureHelper
import com.arghyam.commons.utils.Constants.PHONE_NUMBER
import com.arghyam.iam.ui.profile.ui.ProfileActivity
import com.arghyam.landing.services.SmsListener
import com.arghyam.landing.services.SmsReceiver
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.android.synthetic.main.content_otp_verify.*

class OtpVerifyActivity : AppCompatActivity() {

    lateinit var phoneNumber: String
    var isOtpFilled: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verify)
        init()
    }

    private fun init() {
        setPhoneNumberText()
        implementTextWatcher()
        startSmsRetriever()
        initSubmitButtonClick()
        initBackPressListener()
    }

    private fun initSubmitButtonClick() {
        btnVerify.setOnClickListener(initClickListener())
    }

    private fun initBackPressListener() {
        backPressed.setOnClickListener {
            finish()
        }
    }

    private fun initClickListener(): View.OnClickListener {
        return View.OnClickListener {
            if (isOtpFilled) {
                val intent = Intent(this@OtpVerifyActivity, ProfileActivity::class.java)
                intent.putExtra(PHONE_NUMBER, phoneNumber)
                startActivity(intent)
            }
        }
    }

    private fun setPhoneNumberText() {
        phoneNumber = getIntentStringData(PHONE_NUMBER)
        displayNumber.text = "${getText(R.string.sms_was_sent)} $phoneNumber"
    }

    private fun getIntentStringData(key: String): String {
        return intent.getStringExtra(key)
    }

    private fun implementTextWatcher() {
        onEditTextBackSpaceClicked(otp_1, otp_1)
        onEditTextBackSpaceClicked(otp_2, otp_1)
        onEditTextBackSpaceClicked(otp_3, otp_2)
        onEditTextBackSpaceClicked(otp_4, otp_3)
        onEditTextCodeEntered(otp_1, otp_2)
        onEditTextCodeEntered(otp_2, otp_3)
        onEditTextCodeEntered(otp_3, otp_4)
        onEditTextCodeEntered(otp_4, otp_4)
    }

    private fun startSmsRetriever() {
        val client = SmsRetriever.getClient(this)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            listenOtp()
        }
        task.addOnFailureListener { e ->
            Log.e("CodeActivity", "Failed to start sms retriever: ${e.message}")
        }
        // todo Remove AppSignatureHelper class while building release apk

        /**
         * Print it for app signature and put it in the message receiving it from the server
         **/

        var appSignature = AppSignatureHelper(this)
        appSignature.appSignatures
    }

    private fun listenOtp() {
        SmsReceiver.bindListener(object : SmsListener {
            override fun onSuccess(code: String) {
                displayOtp(code)
            }

            override fun onError() {
                Toast.makeText(
                    this@OtpVerifyActivity,
                    "Error while fetching otp, please enter it manually",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun displayOtp(code: String) {
        otp_1.setText("${code[0]}")
        otp_2.setText("${code[1]}")
        otp_3.setText("${code[2]}")
        otp_4.setText("${code[3]}")
        btnVerify.setBackgroundColor(resources.getColor(R.color.denim))
    }

    private fun onEditTextBackSpaceClicked(from: EditText, to: EditText) {
        from.setOnKeyListener { v, keyCode, event ->
            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (from.text.toString().isEmpty()) {
                    to.requestFocus()
                    to.setSelection(to.text.toString().length)
                    if (otp_1.text.toString().isNotEmpty() && otp_2.text.toString().isNotEmpty() && otp_3.text.toString().isNotEmpty() && otp_4.text.toString().isNotEmpty()) {
                        isOtpFilled = true
                        btnVerify.setBackgroundColor(resources.getColor(R.color.denim))
                    } else {
                        isOtpFilled = false
                        btnVerify.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
                    }
                }
            }
            false
        }
    }

    private fun onEditTextCodeEntered(from: EditText, to: EditText) {
        from.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    to.requestFocus()
                    to.setSelection(to.text.toString().length)
                    if (otp_1.text.toString().isNotEmpty() && otp_2.text.toString().isNotEmpty() && otp_3.text.toString().isNotEmpty() && otp_4.text.toString().isNotEmpty()) {
                        isOtpFilled = true
                        btnVerify.setBackgroundColor(resources.getColor(R.color.denim))
                    } else {
                        isOtpFilled = false
                        btnVerify.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
                    }
                } else {
                    isOtpFilled = false
                    btnVerify.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
                }
            }
        })
    }

}
