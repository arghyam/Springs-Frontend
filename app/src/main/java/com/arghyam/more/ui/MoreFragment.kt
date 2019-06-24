package com.arghyam.more.ui


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig

import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.Constants.GET_USER_PROFILE
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.more.model.GetUserProfileModel
import com.arghyam.more.model.GetUserProfileViewModel
import com.arghyam.more.model.LoggedInUserProfileModel
import com.arghyam.more.model.UserProfileDataDetailsModel
import com.arghyam.more.repository.GetUserProfileRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.content_more.*
import kotlinx.android.synthetic.main.content_more.view.*
import kotlinx.android.synthetic.main.fragment_more.view.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 *
 */
class MoreFragment : Fragment() {

    @Inject
    lateinit var getUserProfileRepository: GetUserProfileRepository
    private var getUserProfileViewModel: GetUserProfileViewModel? = null


    /**
     * Initialize newInstance for passing paameters
     */
    companion object {
        fun newInstance(): MoreFragment {
            var fragmentMore = MoreFragment()
            var args = Bundle()
            fragmentMore.arguments = args
            return fragmentMore
        }

    }

    private fun init() {
        initComponent()
        initClick()
        initRepository()
        getUserProfileRequest()
        initGetUserProfile()

    }
    private fun initComponent() {
        (activity!!.application as ArghyamApplication).getmAppComponent()?.inject(this)
    }

    private fun initClick() {
        edit_icon.setOnClickListener {
            rl_edit_name.visibility = GONE
            edit_name_layout.visibility = VISIBLE

        }
        save_name.setOnClickListener {
            rl_edit_name.visibility = VISIBLE
            edit_name_layout.visibility = GONE
        }

        sign_in_button.setOnClickListener {
            startActivity(Intent(activity!!, LoginActivity::class.java))
            activity!!.finish()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater!!.inflate(R.layout.fragment_more, container, false)
        if (SharedPreferenceFactory(activity!!).getString(Constants.ACCESS_TOKEN) == "") {
            rootView.user_details.visibility = GONE
            rootView.sign_out.visibility = GONE
            rootView.sign_in_for_guest.visibility = VISIBLE
        }
        else{
            rootView.app_bar.visibility = GONE
        }

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun initRepository() {
        getUserProfileViewModel = ViewModelProviders.of(this).get(GetUserProfileViewModel::class.java)
        getUserProfileViewModel?.getUserProfileRepository(getUserProfileRepository)
    }

    private fun getUserProfileRequest() {
        var getAllSpringObject = RequestModel(
            id = GET_USER_PROFILE,
            ver = BuildConfig.VER,
            ets = BuildConfig.ETS,
            params = Params(
                did = "",
                key = "",
                msgid = ""
            ),
            request = GetUserProfileModel(
                person = LoggedInUserProfileModel(
                    phonenumber = "7022973997"
                )
            )
        )
        getUserProfileViewModel?.getUserProfileApi(context!!, getAllSpringObject)
    }

    private fun initGetUserProfile() {
        getUserProfileViewModel?.getUserProfileResponse()?.observe(this, Observer {
            saveUserProfileData(it)
        })
        getUserProfileViewModel?.getUserProfileError()?.observe(this, Observer {
            Log.e("error", it)
            if (getUserProfileViewModel?.getUserProfileError()?.hasObservers()!!) {
                getUserProfileViewModel?.getUserProfileError()?.removeObservers(this)
            }
        })
    }

    private fun saveUserProfileData(responseModel: ResponseModel) {
        if (responseModel.response.responseCode == "200") {
            var responseData: UserProfileDataDetailsModel = Gson().fromJson(
                ArghyamUtils().convertToString(responseModel.response.responseObject),
                object : TypeToken<UserProfileDataDetailsModel>() {}.type
            )
            tv_username.text = responseData.firstName
            tv_user_phone.text = responseData.username
            Log.e(
                "UserProfile", ArghyamUtils().convertToString(responseData.firstName)
            )

        }
    }

}
