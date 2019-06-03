package com.arghyam.iam.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig
import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants.ACCESS_TOKEN
import com.arghyam.commons.utils.Constants.IS_NEW_USER
import com.arghyam.commons.utils.Constants.PHONE_NUMBER
import com.arghyam.commons.utils.Constants.REFRESH_TOKEN
import com.arghyam.commons.utils.Constants.VERIFY_OTP_ID
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.iam.model.*
import com.arghyam.iam.repository.VerifyOtpRepository
import com.arghyam.iam.viewmodel.VerifyOtpViewModel
import com.arghyam.landing.services.SmsListener
import com.arghyam.landing.services.SmsReceiver
import com.arghyam.landing.ui.activity.LandingActivity
import com.arghyam.profile.ui.ProfileActivity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.content_otp_verify.*
import javax.inject.Inject

class OtpVerifyActivity : AppCompatActivity() {


    @Inject
    lateinit var verifyOtpRepository: VerifyOtpRepository

    private var verifyOtpViewModel: VerifyOtpViewModel? = null

    private lateinit var phoneNumber: String
    private var resendOtpCount: Int = 0
    private var maxTime = 30
    private var isCounterRunning: Boolean = false
    private var isTermsChecked: Boolean = false
    private var isAlreadyVerified: Boolean = false

    private lateinit var countDownTimer: CountDownTimer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verify)
        init()
    }

    private fun init() {
        initComponent()
        initRepository()
        setPhoneNumberText()
        implementTextWatcher()
        startSmsRetriever()
        initSubmitButtonClick()
        initBackPressListener()
        initTermsCheckBox()
        initResendCodeButton()
        initResendTimer()
        initApiCalls()
    }


    private fun initComponent() {
        (application as ArghyamApplication).getmAppComponent()?.inject(this)
    }

    private fun initRepository() {
        verifyOtpViewModel = ViewModelProviders.of(this).get(VerifyOtpViewModel::class.java)
        verifyOtpViewModel?.setRepository(verifyOtpRepository)
    }

    private fun initApiCalls() {
        verifyOtpViewModel?.verifyOtpResponse()?.observe(this, Observer {
            saveAccessToken(it)
            if (isAlreadyVerified) {
                val intent = Intent(this@OtpVerifyActivity, ProfileActivity::class.java)
                intent.putExtra(PHONE_NUMBER, phoneNumber)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this@OtpVerifyActivity, LandingActivity::class.java)
                startActivity(intent)
                finish()
            }

        })
        verifyOtpViewModel?.verifyOtpError()?.observe(this, Observer {
            ArghyamUtils().longToast(this@OtpVerifyActivity, it)
        })

        verifyOtpViewModel?.resendOtpResponse()?.observe(this@OtpVerifyActivity, Observer {
            if (it?.response?.responseCode.equals("200")) {
                initResendTimer()
                ArghyamUtils().longToast(this@OtpVerifyActivity, "Otp has been sent to your mobile")
            }
        })
        verifyOtpViewModel?.resendOtpError()?.observe(this@OtpVerifyActivity, Observer {
            ArghyamUtils().longToast(
                    this@OtpVerifyActivity,
                    "There has been some error while resending the otp, Please try again"
            )
        })
    }

    private fun saveAccessToken(it: ResponseModel) {
        var accessTokenResponse: AccessTokenModel = Gson().fromJson(
                ArghyamUtils().convertToString(it.response.responseObject),
                object : TypeToken<AccessTokenModel>() {}.type
        )
        SharedPreferenceFactory(this@OtpVerifyActivity).setString(
                ACCESS_TOKEN,
                accessTokenResponse.accessTokenResponseDTO.access_token
        )
        SharedPreferenceFactory(this@OtpVerifyActivity).setString(
                REFRESH_TOKEN,
                accessTokenResponse.accessTokenResponseDTO.refresh_token
        )
    }

    private fun initResendCodeButton() {
        resendCode.setOnClickListener {
            if (resendOtpCount < 4) {
                if (!isCounterRunning) {
                    makeResendOtpCall()
                }
            } else {
                Toast.makeText(
                        this@OtpVerifyActivity,
                        "You have reached the maximum limit, Please try again",
                        Toast.LENGTH_LONG
                ).show()
                onBackPressed()
            }
        }
    }

    private fun makeResendOtpCall() {
        var requestModel = RequestModel(
                id = VERIFY_OTP_ID,
                ver = BuildConfig.VER,
                ets = BuildConfig.ETS,
                params = Params(
                        did = "",
                        key = "",
                        msgid = ""
                ),
                request = RequestPersonDataModel(
                        person = PersonModel(
                                username = phoneNumber.substring(4, phoneNumber.length)
                        )
                )
        )
        verifyOtpViewModel?.resendOtpApi(this@OtpVerifyActivity, requestModel)
    }

    private fun initResendTimer() {
        resendCode.alpha = 0.5f
        isCounterRunning = true
        countDownTimer = object : CountDownTimer(30000, 1000) {
            override fun onFinish() {
                resendCode.text = "${getString(R.string.resend)}"
                maxTime = 30
                resendOtpCount++
                isCounterRunning = false
                resendCode.alpha = 1.0f
            }

            override fun onTick(millisUntilFinished: Long) {
                resendCode.text = "${getString(R.string.resend)} (00:${ArghyamUtils().checkDigit(maxTime)})"
                maxTime--
            }
        }.start()
    }

    private fun initTermsCheckBox() {
        isTermsChecked = !getIntentBooleanData(IS_NEW_USER)
        isAlreadyVerified = getIntentBooleanData(IS_NEW_USER)
        if (isTermsChecked) {
            layout_checkbox.visibility = GONE
        } else {
            layout_checkbox.visibility = VISIBLE
            termsCheckBox.setOnCheckedChangeListener { _, isChecked ->
                isTermsChecked = isChecked
                if (isChecked && isOtpEditTextFilled()) {
                    btnVerify.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
                } else {
                    btnVerify.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
                }
            }
        }
    }

    private fun initSubmitButtonClick() {
        btnVerify.setOnClickListener(initClickListener())
    }

    private fun initBackPressListener() {
        backPressed.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initClickListener(): View.OnClickListener {
        return View.OnClickListener {
            if (isOtpEditTextFilled() && isTermsChecked) {
                verifyOtpApiCall()
            }
        }
    }

    private fun verifyOtpApiCall() {
        var requestModel = RequestModel(
                id = VERIFY_OTP_ID,
                ver = BuildConfig.VER,
                ets = BuildConfig.ETS,
                params = Params(
                        did = "",
                        key = "",
                        msgid = ""
                ),
                request = RequestPersonDataModel(
                        person = VerifyOtpRequestModel(
                                phoneNumber = phoneNumber.substring(4, phoneNumber.length),
                                otp = getOtp()
                        )
                )
        )
        verifyOtpViewModel?.verifyOtpApi(this@OtpVerifyActivity, requestModel)
    }

    private fun setPhoneNumberText() {
        phoneNumber = getIntentStringData(PHONE_NUMBER)
        displayNumber.text = "${getText(R.string.sms_was_sent)} $phoneNumber"
    }

    private fun getOtp(): String {
        return "${otp_1.text}${otp_2.text}${otp_3.text}${otp_4.text}"
    }

    private fun getIntentStringData(key: String): String {
        return intent.getStringExtra(key)
    }

    private fun getIntentBooleanData(key: String): Boolean {
        return intent.getBooleanExtra(key, false)
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

//        var appSignature = AppSignatureHelper(this)
//        Log.e("ste", appSignature.appSignatures.toString())
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
        countDownTimer.cancel()
        isCounterRunning = false
        resendCode.text = "${getString(R.string.resend)}"
        resendCode.alpha = 1.0f
    }

    private fun onEditTextBackSpaceClicked(from: EditText, to: EditText) {
        from.setOnKeyListener { _, keyCode, _ ->
            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (from.text.toString().isEmpty()) {
                    to.requestFocus()
                    to.setSelection(to.text.toString().length)
                    if (isOtpEditTextFilled() && isTermsChecked) {
                        btnVerify.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
                    } else {
                        btnVerify.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
                    }
                } else {
                    btnVerify.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
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
                    if (isOtpEditTextFilled() && isTermsChecked) {
                        btnVerify.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
                    } else {
                        btnVerify.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
                    }
                } else {
                    btnVerify.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
                }
            }
        })
    }

    private fun isOtpEditTextFilled(): Boolean {
        return otp_1.text.toString().isNotEmpty() && otp_2.text.toString().isNotEmpty()
                && otp_3.text.toString().isNotEmpty() && otp_4.text.toString().isNotEmpty()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        var intent = Intent(this@OtpVerifyActivity, LoginActivity::class.java)
        intent.putExtra(PHONE_NUMBER, phoneNumber)
        startActivity(intent)
        finish()
    }

}

