package com.arghyam.admin.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.widget.EditText
import androidx.lifecycle.ViewModelProviders
import com.arghyam.ArghyamApplication
import com.arghyam.R
import com.arghyam.additionalDetails.repository.AdditionalDetailsRepository
import com.arghyam.additionalDetails.viewmodel.AddAdditionalDetailsViewModel
import com.arghyam.admin.adapter.ExpandableListAdapter
import com.arghyam.admin.model.AllUsersDataModel
import com.arghyam.admin.model.AllUsersDetailsModel
import com.arghyam.admin.repository.GetRegisteredUsersRepository
import com.arghyam.admin.viewmodel.GetRegisteredUsersViewModel
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.landing.model.AllSpringDetailsModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_admin_panel.*
import kotlinx.android.synthetic.main.activity_spring_details.*
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject

class AdminPanelActivity : AppCompatActivity() {

    private var user: ArrayList<User> = ArrayList()
    private lateinit var mGetRegisteredUsersViewModel: GetRegisteredUsersViewModel

    @Inject
    lateinit var mRegisteredUsersRepository: GetRegisteredUsersRepository
    lateinit var listAdapter: ExpandableListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)
        init()
    }

    private fun init() {
        initComponent()
        initRepository()
        observeData()
        makeApiCall()
        initToolBar()
        textChangeListenerForSearchEditText()
        clearSearchEditText()
    }

    private fun initRepository() {
        mGetRegisteredUsersViewModel = ViewModelProviders.of(this).get(GetRegisteredUsersViewModel::class.java)
        mGetRegisteredUsersViewModel.setegisteredUsersRepository(mRegisteredUsersRepository)
    }

    private fun observeData() {
        mGetRegisteredUsersViewModel.getAdditionalDataSuccess().observe(this, androidx.lifecycle.Observer {
            Log.d("registeredUsers", "success")
            initData(it)
        })

        mGetRegisteredUsersViewModel.getAdditionalDataError().observe(this, androidx.lifecycle.Observer {
            Log.d("registeredUsers", "Api Error")
        })
    }

    private fun saveRegisteredUsers(responseModel: ResponseModel) {
        if (responseModel.response.responseCode == "200") {
            var responseData: List<AllUsersDataModel> = Gson().fromJson(
                ArghyamUtils().convertToString(responseModel.response.responseObject),
                object : TypeToken<List<AllUsersDataModel>>() {}.type
            )
            var i: Int = 0
            while (i < responseData.size) {
                user.add(
                    User(
                        responseData[i].firstName,
                        responseData[i].username,
                        mutableListOf("Admin", "Reviewer")
                    )
                )
                i++
            }
//            listAdapter.notifyDataSetChanged()
        }
    }

    private fun clearSearchEditText() {
        user_search_input.onRightDrawableClicked {
            user_search_input.text.clear()
        }
    }

    private fun makeApiCall() {
        mGetRegisteredUsersViewModel.getRegisteredUsersApi(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
        this.setOnTouchListener { v, event ->
            var hasConsumed = false
            if (v is EditText && event.x >= v.width - v.totalPaddingRight) {
                if (event.action == MotionEvent.ACTION_UP) {
                    onClicked(this)
                }
                hasConsumed = true
            }
            hasConsumed
        }
    }


    private fun textChangeListenerForSearchEditText() {
        user_search_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.isEmpty()!!) {
                    user_search_input.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_search, 0)
                } else {
                    user_search_input.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_close, 0)
                }
            }

        })
    }

    private fun initData(responseModel: ResponseModel) {
//        loadData()
        saveRegisteredUsers(responseModel)
        listAdapter = ExpandableListAdapter(this, user as ArrayList<com.arghyam.admin.adapter.User>)
        user_list.setAdapter(listAdapter)
    }

    private fun loadData() {
        user.add(User("username1", "+91 9897969594", mutableListOf("Admin", "Reviewer")))
        user.add(User("username2", "+91 9897969594", mutableListOf("Admin")))
        user.add(User("username3", "+91 9897969594", mutableListOf("Admin")))
        user.add(User("username4", "+91 9897969594", mutableListOf("Admin", "Reviewer")))
        user.add(User("username5", "+91 9897969594", mutableListOf("Admin", "Reviewer")))
        user.add(User("username6", "+91 9897969594", mutableListOf()))
        user.add(User("username7", "+91 9897969594", mutableListOf()))
        user.add(User("username8", "+91 9897969594", mutableListOf()))
        user.add(User("username9", "+91 9897969594", mutableListOf()))
        user.add(User("username10", "+91 9897969594", mutableListOf()))
        user.add(User("username11", "+91 9897969594", mutableListOf()))
        user.add(User("username12", "+91 9897969594", mutableListOf()))
        user.add(User("username13", "+91 9897969594", mutableListOf()))


    }

    private fun initToolBar() {
        setSupportActionBar(admin_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initComponent() {
        (this.application as ArghyamApplication).getmAppComponent()?.inject(this)
    }
}


data class User(var username: String, var phoneNumber: String, var role: List<String>?)