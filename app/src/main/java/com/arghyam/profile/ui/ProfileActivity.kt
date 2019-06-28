package com.arghyam.profile.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig
import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants.PHONE_NUMBER
import com.arghyam.commons.utils.Constants.UPDATE_USER_PROFILE_ID
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.RequestPersonDataModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.landing.ui.activity.LandingActivity
import com.arghyam.profile.model.UserProfileModel
import com.arghyam.profile.repository.ProfileRepository
import com.arghyam.profile.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.content_profile.*
import javax.inject.Inject

class ProfileActivity : AppCompatActivity() {

    @Inject
    lateinit var profileRepository: ProfileRepository

    private var profileViewModel: ProfileViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        init()
    }


    private fun init() {
        initComponent()
        extractBundle()
        initRepository()
        initSubmit()
        initApiCalls()
        initNameEditText()
        initBackPressListener()
    }

    private fun initComponent() {
        (application as ArghyamApplication).getmAppComponent()?.inject(this)
    }

    private fun initApiCalls() {
        profileViewModel?.getProfileResponse()?.observe(this, Observer {
            saveUserProfileData(it)
        })
        profileViewModel?.getProfileError()?.observe(this, Observer {
            Log.e("error", it)

        })
    }

    private fun saveUserProfileData(responseModel: ResponseModel) {
        Log.d("saveUserProfileData", "saveUserProfileData")
        if (responseModel.response.responseCode.equals("200")) {
            val intent = Intent(this@ProfileActivity, LandingActivity::class.java)
            startActivity(intent)
            finish()
        }
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

            if (fullName.text == null || fullName.text.toString().trim().equals("")) {
                ArghyamUtils().longToast(this@ProfileActivity, "Please enter the full name")
            } else if( fullName.text.toString().trim().length < 3){
                ArghyamUtils().longToast(this@ProfileActivity, "Full name should contain atleast 3 characters")

            } else if( fullName.text.toString().startsWith(" ")){
                ArghyamUtils().longToast(this@ProfileActivity, "Spring name should not start with space")
            }
            else {
                updateUserProfileOnClickListener()
            }


        }
    }

    private fun updateUserProfileOnClickListener() {
        var userProfileObject = RequestModel(
            id = UPDATE_USER_PROFILE_ID,
            ver = BuildConfig.VER,
            ets = BuildConfig.ETS,
            params = Params(
                did = "",
                key = "",
                msgid = ""
            ),
            request = RequestPersonDataModel(
                person = UserProfileModel(
                    name = fullName.text.toString().trim(),
                    phonenumber = mobile.text.toString().substring(
                        4,
                        mobile.text!!.length
                    )
                )
            )
        )
        profileViewModel?.userProfileApi(this, userProfileObject)
    }

    private fun textChangedListener(): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if((s.toString().startsWith(" "))){
                    submit.setBackgroundColor(resources.getColor(R.color.cornflower_blue))
                }
                else if (s.toString().length >= 3 ) {
                    submit.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
                }  else {
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

    private fun initRepository() {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        profileViewModel?.setProfileRepository(profileRepository)
    }

}
