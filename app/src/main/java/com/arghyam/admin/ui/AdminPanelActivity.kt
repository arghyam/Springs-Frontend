package com.arghyam.admin.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig
import com.arghyam.R
import com.arghyam.admin.adapter.ExpandableListAdapter
import com.arghyam.admin.interfaces.AdminPanelInterface
import com.arghyam.admin.model.AllUsersDataModel
import com.arghyam.admin.model.RolesModel
import com.arghyam.admin.model.UserRolesModel
import com.arghyam.admin.repository.AssignRolesRepository
import com.arghyam.admin.repository.GetRegisteredUsersRepository
import com.arghyam.admin.viewmodel.AssignRoleViewModel
import com.arghyam.admin.viewmodel.GetRegisteredUsersViewModel
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.OnFocusLostListener
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
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

    private lateinit var mAssignRoleViewModel: AssignRoleViewModel
    @Inject
    lateinit var mAssignRolesRepository: AssignRolesRepository

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
        user_search_input.hideSoftKeyboardOnFocusLostEnabled(true)
        textChangeListenerForSearchEditText()
        clearSearchEditText()
    }

    private fun EditText.hideSoftKeyboardOnFocusLostEnabled(enabled: Boolean) {
        val listener = if (enabled)
            OnFocusLostListener()
        else
            null
        onFocusChangeListener = listener
    }

    private fun initRepository() {
        mGetRegisteredUsersViewModel = ViewModelProviders.of(this).get(GetRegisteredUsersViewModel::class.java)
        mGetRegisteredUsersViewModel.setRegisteredUsersRepository(mRegisteredUsersRepository)

        mAssignRoleViewModel = ViewModelProviders.of(this).get(AssignRoleViewModel::class.java)
        mAssignRoleViewModel.setAssignRoleRepository(mAssignRolesRepository)

    }

    private fun observeData() {
        mGetRegisteredUsersViewModel.getAdditionalDataSuccess().observe(this, androidx.lifecycle.Observer {
            Log.d("registeredUsers", "success")
            initData(it)
        })

        mGetRegisteredUsersViewModel.getAdditionalDataError().observe(this, androidx.lifecycle.Observer {
            Log.d("registeredUsers", "Api Error")
        })

        mAssignRoleViewModel.assignRoleSuccess().observe(this, androidx.lifecycle.Observer {
            Log.d("assign role", "success")
            roleData(it)
        })

        mAssignRoleViewModel.assignRoleError().observe(this, androidx.lifecycle.Observer {
            Log.d("assign role", "Api Error")
        })
    }

    private fun roleData(it: ResponseModel?) {
        if (null!=it) {
            if ("200"==it.response.responseCode) {
                makeApiCall()
            }
        }
    }

    private fun saveRegisteredUsers(responseModel: ResponseModel) {
        user.clear()
        if (responseModel.response.responseCode == "200") {
            var responseData: List<AllUsersDataModel> = Gson().fromJson(
                ArghyamUtils().convertToString(responseModel.response.responseObject),
                object : TypeToken<List<AllUsersDataModel>>() {}.type
            )
            var i = 0
            while (i < responseData.size) {
                if (responseData[i].firstName != null && responseData[i].username != null && responseData[i].username!="admin" ) {
                    user.add(
                        User(
                            responseData[i].firstName,
                            responseData[i].username,
                            responseData[i].admin,
                            responseData[i].reviewer,
                            responseData[i].id
                        )
                    )
                }
                i++
            }
        }
        oneListExpand()
        admin_progress_bar.visibility = GONE
    }

    private fun oneListExpand() {
        val expandableListView = findViewById<ExpandableListView>(R.id.user_list)
        expandableListView.setOnGroupExpandListener(object : ExpandableListView.OnGroupExpandListener {
            var previousItem = -1

            override fun onGroupExpand(groupPosition: Int) {
                if (groupPosition != previousItem)
                    expandableListView.collapseGroup(previousItem)
                previousItem = groupPosition
            }
        })
    }

    private fun clearSearchEditText() {
        user_search_input.onRightDrawableClicked {
            user_search_input.text.clear()

        }
    }

    private fun sendRequestForRoles(role: String, id: String) {
        var userRole = ""
        if ("admin" == role)
            userRole = "Arghyam-admin"
        else if ("reviewer" == role)
            userRole = "Arghyam-reviewer"
        var mRequestData = SharedPreferenceFactory(this).getString(Constants.USER_ID)?.let {
            UserRolesModel(
                userId = id,
                role = userRole,
                admin = it
            )
        }?.let {
            RolesModel(
                roles = it
            )
        }?.let {
            RequestModel(
                id = "forWater.user.createDischargeData",
                ver = BuildConfig.VER,
                ets = BuildConfig.ETS,
                params = Params(
                    did = "",
                    key = "",
                    msgid = ""
                ),
                request = it
            )
        }
        makeApiCallForAssignRole(mRequestData)
    }

    private fun makeApiCallForAssignRole(mRequestData: RequestModel?) {
        if (mRequestData != null) {
            mAssignRoleViewModel.assignRoleApi(this, mRequestData)
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
            if (s.username!!.toLowerCase().contains(text.toLowerCase()) || s.phoneNumber!!.contains(text)) {
                filterName.add(s)
            }
        }
        listAdapter.filterList(filterName as ArrayList<com.arghyam.admin.adapter.User>)
    }

    private fun initData(responseModel: ResponseModel) {
        saveRegisteredUsers(responseModel)
        listAdapter =
            ExpandableListAdapter(this, user as ArrayList<com.arghyam.admin.adapter.User>, adminPanelInterface)
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

    private var adminPanelInterface: AdminPanelInterface = object : AdminPanelInterface {
        override fun onCheckBoxListener(role: String, id: String) {
            admin_progress_bar.visibility = VISIBLE
            Log.e("adminPanel",role+"        "+id)
            sendRequestForRoles(role, id)
        }
    }
}


data class User(var username: String?, var phoneNumber: String?, var admin: Boolean, var reviewer: Boolean, var id: String?)
