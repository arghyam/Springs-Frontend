package com.arghyam.more.ui


import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig
import com.arghyam.FAQS.ui.FaqActivity
import com.arghyam.R
import com.arghyam.about.AboutActivity
import com.arghyam.admin.ui.AdminPanelActivity
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.Constants.ACCESS_TOKEN
import com.arghyam.commons.utils.Constants.GET_USER_PROFILE
import com.arghyam.commons.utils.Constants.UPDATE_USER_PROFILE
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.help.ui.HelpActivity
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.more.model.*
import com.arghyam.more.repository.GetUserProfileRepository
import com.arghyam.more.repository.UpdateUserProfileRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.content_more.*
import kotlinx.android.synthetic.main.content_more.view.*
import kotlinx.android.synthetic.main.fragment_more.*
import kotlinx.android.synthetic.main.fragment_more.view.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 *
 */
class MoreFragment : Fragment() {

    @Inject
    lateinit var getUserProfileRepository: GetUserProfileRepository
    @Inject
    lateinit var updateUserProfileRepository: UpdateUserProfileRepository
    private var getUserProfileViewModel: GetUserProfileViewModel? = null
    private var updateUserProfileViewModel: UpdateUserProfileViewModel? = null
    private lateinit var responseData: UserProfileDataDetailsModel

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
        val toolbar = toolbar as Toolbar
        toolbar.title = "More"
    }

    private fun initClick() {
        edit_icon.setOnClickListener {
            rl_edit_name.visibility = GONE
            save_name.setText(responseData.firstName)
            edit_name_layout.visibility = VISIBLE

        }
        save_name.setOnClickListener {

            if (save_name.text == null || save_name.text.toString().trim().equals("")) {
                ArghyamUtils().longToast(context!!, "Please enter name")
            } else if (save_name.text.toString().trim().length < 3) {
                ArghyamUtils().longToast(context!!, "Name should contain atleast 3 characters")
            } else if( save_name.text.toString().startsWith(" ")){
                ArghyamUtils().longToast(context!!, "Full name should not start with space")
            } else {
                initUpdateProfile()
                rl_edit_name.visibility = VISIBLE
                edit_name_layout.visibility = GONE
            }
        }

        sign_in_button.setOnClickListener {
            startActivity(Intent(activity!!, LoginActivity::class.java))
            activity!!.finish()
        }

        admin_layout.setOnClickListener {
            startActivity(Intent(activity!!, AdminPanelActivity::class.java))
        }
        sign_out.setOnClickListener {
            showDialog(it)
        }
        about_layout.setOnClickListener      {
            startActivity(Intent(activity!!, AboutActivity::class.java))
        }
        faq_layout.setOnClickListener {
            startActivity(Intent(activity!!, FaqActivity::class.java))

        }
        help_layout.setOnClickListener {
            startActivity(Intent(activity!!, HelpActivity::class.java))

        }


    }

    private fun showDialog(it: View?) {

//        ArghyamUtils().AlertBox(activity!!.applicationContext,activity as AppCompatActivity,"Are you sure ?","Do you want to Sign Out?",LoginActivity::class.java)
        val dialogBuilder = AlertDialog.Builder(context!!)
        dialogBuilder.setMessage("Do you want to sign out")

            .setPositiveButton("YES") { dialog, which ->

                if (SharedPreferenceFactory(activity!!.applicationContext).getString(ACCESS_TOKEN) != "") {
                    SharedPreferenceFactory(activity!!.applicationContext).setString(ACCESS_TOKEN, "")
                }
                startActivity(Intent(activity!!, LoginActivity::class.java))
                activity!!.finish()
                dialog.cancel()
            }
            .setNegativeButton("CANCEL") { dialog, which ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.setTitle("Are you sure ?")
        alert.show()


    }

    private fun initUpdateProfile() {
        initUpdateUserProfileRepository()
        updateUserProfileRequest()
        initUpdateUserProfile()
    }

    private fun initUpdateUserProfileRepository() {
        updateUserProfileViewModel = ViewModelProviders.of(this).get(UpdateUserProfileViewModel::class.java)
        updateUserProfileViewModel?.updateUserProfileRepository(updateUserProfileRepository)
    }

    private fun updateUserProfileRequest() {

       var userId = SharedPreferenceFactory(context!!).getString(Constants.USER_ID)!!

        var updateUserProfileObject = RequestModel(
            id = UPDATE_USER_PROFILE,
            ver = BuildConfig.VER,
            ets = BuildConfig.ETS,
            params = Params(
                did = "",
                key = "",
                msgid = ""
            ),
            request = UpdateUserProfileModel(
                person = UpdateLoggedInUserProfileModel(
                    name = save_name.text.toString().trim(),
                    phonenumber = tv_user_phone.text.toString()
                )
            )
        )
        updateUserProfileViewModel?.getUserProfileApi(context!!, userId , updateUserProfileObject)
    }

    private fun initUpdateUserProfile() {
        updateUserProfileViewModel?.updateUserProfileResponse()?.observe(this, Observer {
            updateUserProfileData(it)
        })
        updateUserProfileViewModel?.updateUserProfileError()?.observe(this, Observer {
            Log.e("error", it)
        })

        if (updateUserProfileViewModel?.updateUserProfileError()?.hasObservers()!!) {
            updateUserProfileViewModel?.updateUserProfileError()?.removeObservers(this)
        }
    }

    private fun updateUserProfileData(responseModel: ResponseModel) {
        if (responseModel.response.responseCode == "200") {
            getUserProfileRequest()
            initGetUserProfile()
//            tv_username.text = responseData.firstName
//            tv_user_phone.text = responseData.username
            Log.e(
                "Anirudh User", ArghyamUtils().convertToString(responseData.firstName)
            )

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(R.layout.fragment_more, container, false)
        if (SharedPreferenceFactory(activity!!).getString(Constants.ACCESS_TOKEN) == "") {
            rootView.user_details.visibility = GONE
            rootView.sign_out.visibility = GONE
            rootView.sign_in_for_guest.visibility = VISIBLE
            rootView.admin_layout.visibility = GONE
        } else {
            rootView.toolbar.visibility = GONE
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
        Log.e("Anirudh", context?.let { SharedPreferenceFactory(it).getString(Constants.USER_PHONE) })
        var getUserProfileObject = RequestModel(
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
                    phonenumber = context?.let { SharedPreferenceFactory(it).getString(Constants.USER_PHONE) }!!
                )
            )
        )
        getUserProfileViewModel?.getUserProfileApi(context!!, getUserProfileObject)
    }

    private fun initGetUserProfile() {
        getUserProfileViewModel?.getUserProfileResponse()?.observe(this, Observer {
            saveUserProfileData(it)
        })
        getUserProfileViewModel?.getUserProfileError()?.observe(this, Observer {
            Log.e("error", it)
        })
        if (getUserProfileViewModel?.getUserProfileError()?.hasObservers()!!) {
            getUserProfileViewModel?.getUserProfileError()?.removeObservers(this)
        }

    }


    private fun saveUserProfileData(responseModel: ResponseModel) {
        if (responseModel.response.responseCode == "200") {
            responseData = Gson().fromJson(
                ArghyamUtils().convertToString(responseModel.response.responseObject),
                object : TypeToken<UserProfileDataDetailsModel>() {}.type
            )
            tv_username.text = responseData.firstName
            tv_user_phone.text = responseData.username
        }
    }

}
