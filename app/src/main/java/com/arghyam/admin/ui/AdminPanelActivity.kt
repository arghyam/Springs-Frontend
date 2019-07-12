package com.arghyam.admin.ui

import android.annotation.SuppressLint
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
import com.arghyam.admin.adapter.ExpandableListAdapter
import com.arghyam.admin.model.AllUsersDataModel
import com.arghyam.admin.repository.GetRegisteredUsersRepository
import com.arghyam.admin.viewmodel.GetRegisteredUsersViewModel
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.iam.model.ResponseModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_admin_panel.*
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
        Log.e("response", ArghyamUtils().convertToString(responseModel.response.responseObject))
        if (responseModel.response.responseCode == "200") {
            var responseData: List<AllUsersDataModel> = Gson().fromJson(
                ArghyamUtils().convertToString(responseModel.response.responseObject),
                object : TypeToken<List<AllUsersDataModel>>() {}.type
            )
            var i = 0
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
                filter(s.toString())
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

    fun filter(text: String) {
        val filterName = java.util.ArrayList<User>()
        for (s in user) {
            if (s.username!!.toLowerCase().contains(text.toLowerCase())||s.phoneNumber!!.contains(text)) {
                filterName.add(s)
            }
        }
        listAdapter.filterList(filterName as ArrayList<com.arghyam.admin.adapter.User>)
    }

    private fun initData(responseModel: ResponseModel) {
        saveRegisteredUsers(responseModel)
        listAdapter = ExpandableListAdapter(this, user as ArrayList<com.arghyam.admin.adapter.User>)
        user_list.setAdapter(listAdapter)
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


data class User(var username: String?, var phoneNumber: String?, var role: List<String>?)