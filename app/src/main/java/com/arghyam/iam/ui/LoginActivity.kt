package com.arghyam.iam.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig
import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants.IS_NEW_USER
import com.arghyam.commons.utils.Constants.PHONE_NUMBER
import com.arghyam.iam.model.*
import com.arghyam.iam.repository.IamRepository
import com.arghyam.iam.viewmodel.IamViewModel
import com.arghyam.landing.ui.activity.LandingActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.content_login.*
import javax.inject.Inject


class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var iamRepository: IamRepository

    private var iamViewModel: IamViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        (application as ArghyamApplication).getmAppComponent()?.inject(this)
        init()
    }

    private fun init() {
        initMobileInput()
        initGetOtp()
        initRepository()
        initApiCalls()
    }

    private fun initApiCalls() {
        iamViewModel?.getLoginResponse()?.observe(this, Observer {
            //TODO( "Please fix me create shared preference manager @karthik and @stefy ")
            saveUserData(it)
        })
        iamViewModel?.getLoginError()?.observe(this, Observer {
            Log.e("error", it)
        })

    }

    private fun gotoOtpActivity(isNewUser: Boolean) {
        var intent = Intent(this@LoginActivity, OtpVerifyActivity::class.java)
        intent.putExtra(PHONE_NUMBER, inputNumber.text.toString().trim())
        intent.putExtra(IS_NEW_USER, isNewUser)
        startActivity(intent)
    }

    private fun saveUserData(responseModel: ResponseModel) {
        var loginResponseObject: LoginResponseObject = Gson().fromJson(
            ArghyamUtils().convertToString(responseModel.response.responseObject),
            object : TypeToken<LoginResponseObject>() {}.type
        )
        gotoOtpActivity(loginResponseObject.newUserCreated)
    }

    private fun initMobileInput() {
        inputNumber.setText("+91 ")
        inputNumber.addTextChangedListener(mobileNumberInputListener())
    }


    private fun initGetOtp() {
        sendOtpButton.setOnClickListener(getOtpOnClickListener())
        guestBrowse.setOnClickListener {
            startActivity(Intent(this@LoginActivity, LandingActivity::class.java))
        }
    }

    private fun mobileNumberInputListener(): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.toString().startsWith("+91 ")) {
                    inputNumber.setText("+91 ")
                    inputNumber.text?.length?.let { Selection.setSelection(inputNumber.text, it) }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length == 14) {
                    sendOtpButton.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
                } else {
                    sendOtpButton.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
                }
            }

        }
    }

    private fun getOtpOnClickListener(): View.OnClickListener {
        return View.OnClickListener {
            if (inputNumber.text.toString().length == 14) {
                var loginObject = RequestModel(
                    id = BuildConfig.ID,
                    ver = BuildConfig.VER,
                    ets = BuildConfig.ETS,
                    params = Params(
                        did = "",
                        key = "",
                        msgid = ""
                    ),
                    request = RequestPersonDataModel(
                        person = PersonModel(
                            username = inputNumber.text.toString().substring(
                                4,
                                inputNumber.text!!.length
                            )
                        )
                    )
                )
                iamViewModel?.userLoginApi(this, loginObject)
            } else {
                Toast.makeText(this@LoginActivity, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initRepository() {
        iamViewModel = ViewModelProviders.of(this).get(IamViewModel::class.java)
        iamViewModel?.setIamRepository(iamRepository)
    }

}
